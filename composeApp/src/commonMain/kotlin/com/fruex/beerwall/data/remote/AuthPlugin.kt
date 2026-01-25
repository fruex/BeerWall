package com.fruex.beerwall.data.remote

import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.data.local.currentTimeSeconds
import com.fruex.beerwall.data.local.decodeTokenPayload
import com.fruex.beerwall.data.local.ensureTimestamp
import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.getPlatform
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Ktor plugin for automatic token refresh on 401 Unauthorized.
 *
 * This plugin intercepts 401 responses, refreshes the access token using the refresh token,
 * and automatically retries the original request with the new token.
 */
class AuthPlugin private constructor(
    private val tokenManager: TokenManager,
    private val onRefreshFailed: suspend () -> Unit
) {

    companion object : HttpClientPlugin<Configuration, AuthPlugin> {
        override val key = AttributeKey<AuthPlugin>("AuthPlugin")
        private val refreshMutex = Mutex()

        // Endpoints that should NOT have Bearer token attached
        private val publicEndpoints = setOf(
            ApiRoutes.Auth.GOOGLE_SIGN_IN,
            ApiRoutes.Auth.SIGN_IN,
            ApiRoutes.Auth.REGISTER,
            ApiRoutes.Auth.FORGOT_PASSWORD,
            ApiRoutes.Auth.REFRESH_TOKEN
        )

        override fun prepare(block: Configuration.() -> Unit): AuthPlugin {
            val config = Configuration().apply(block)
            return AuthPlugin(config.tokenManager, config.onRefreshFailed)
        }

        override fun install(plugin: AuthPlugin, scope: HttpClient) {
            val platform = getPlatform()
            // Validate host to prevent token leakage
            val apiHost = Url(com.fruex.beerwall.BuildKonfig.BASE_URL).host

            scope.plugin(HttpSend).intercept { request ->
                val initialToken = plugin.tokenManager.getToken()

                // Check if this is a public endpoint
                val isPublicEndpoint = publicEndpoints.any { endpoint ->
                    request.url.encodedPath.endsWith(endpoint)
                }

                // Check if the request is to our API host
                val isApiHost = request.url.host == apiHost

                // Add token to request only if it is API host AND not a public endpoint
                if (isApiHost && !isPublicEndpoint) {
                    initialToken?.let { token ->
                        request.headers[HttpHeaders.Authorization] = "Bearer $token"
                    }
                }

                val originalCall = execute(request)

                // If 401, try to refresh token and retry
                if (originalCall.response.status == HttpStatusCode.Unauthorized) {
                    // Skip refresh if no token was present (unauthorized by design or not logged in)
                    if (initialToken == null) {
                        return@intercept originalCall
                    }

                    platform.log("⚠️ 401 Unauthorized - attempting token refresh", "AuthPlugin", LogSeverity.WARN)

                    val refreshSuccess = refreshMutex.withLock {
                        val currentToken = plugin.tokenManager.getToken()
                        if (currentToken != null && currentToken != initialToken) {
                            // Token already refreshed by another request
                            platform.log("✅ Token already refreshed by another request - retrying", "AuthPlugin", LogSeverity.INFO)
                            true
                        } else if (currentToken == null) {
                            // Tokens were cleared by another request that failed refresh
                            platform.log("❌ Tokens were cleared by another request - refresh failed", "AuthPlugin", LogSeverity.ERROR)
                            false
                        } else {
                            plugin.refreshToken()
                        }
                    }

                    if (refreshSuccess) {
                        // Retry with new token
                        plugin.tokenManager.getToken()?.let { newToken ->
                            request.headers[HttpHeaders.Authorization] = "Bearer $newToken"
                        }
                        execute(request)
                    } else {
                        platform.log("❌ Token refresh failed - calling onRefreshFailed", "AuthPlugin", LogSeverity.ERROR)
                        plugin.onRefreshFailed()
                        originalCall
                    }
                } else {
                    originalCall
                }
            }
        }
    }

    class Configuration {
        lateinit var tokenManager: TokenManager
        var onRefreshFailed: suspend () -> Unit = {}
    }

    private suspend fun refreshToken(): Boolean {
        val platform = getPlatform()

        if (tokenManager.isRefreshTokenExpired()) {
            val expires = tokenManager.getRefreshTokenExpires()
            val now = currentTimeSeconds()
            platform.log("❌ Refresh token expired (expires: $expires, now: $now)", "AuthPlugin", LogSeverity.ERROR)
            return false
        }

        val refreshTokenValue = tokenManager.getRefreshToken()
        if (refreshTokenValue == null) {
            platform.log("❌ No refresh token available", "AuthPlugin", LogSeverity.ERROR)
            return false
        }

        return try {
            // Call refresh token endpoint directly (avoid circular dependency with BaseApiClient)
            val client = HttpClient {
                install(ContentNegotiation) {
                    json(HttpClientFactory.json)
                }
            }

            val response: HttpResponse = client.get("${com.fruex.beerwall.BuildKonfig.BASE_URL}/mobile/auth/refreshToken") {
                header(HttpHeaders.Authorization, "Bearer $refreshTokenValue")
            }

            client.close()

            when (response.status) {
                HttpStatusCode.OK -> {
                    val refreshResponse: com.fruex.beerwall.data.remote.dto.auth.RefreshTokenResponse = response.body()

                    // Decode user data from new token payload
                    val payload = decodeTokenPayload(refreshResponse.token)
                    val firstName = payload["firstName"]
                    val lastName = payload["lastName"]

                    tokenManager.saveTokens(
                        tokens = AuthTokens(
                            token = refreshResponse.token,
                            tokenExpires = ensureTimestamp(refreshResponse.tokenExpires),
                            refreshToken = refreshResponse.refreshToken,
                            refreshTokenExpires = ensureTimestamp(refreshResponse.refreshTokenExpires),
                            firstName = firstName,
                            lastName = lastName
                        )
                    )
                    platform.log("✅ Token refreshed successfully", "AuthPlugin", LogSeverity.INFO)
                    true
                }
                HttpStatusCode.Unauthorized -> {
                    platform.log("❌ Refresh token expired or invalid (401)", "AuthPlugin", LogSeverity.ERROR)
                    false
                }
                else -> {
                    platform.log("❌ Token refresh failed: ${response.status}", "AuthPlugin", LogSeverity.ERROR)
                    false
                }
            }
        } catch (e: Exception) {
            platform.log("❌ Token refresh exception: ${e.message}", "AuthPlugin", LogSeverity.ERROR)
            false
        }
    }
}
