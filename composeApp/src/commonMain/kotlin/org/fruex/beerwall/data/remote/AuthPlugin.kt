package org.fruex.beerwall.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.getPlatform
import org.fruex.beerwall.log

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

        override fun prepare(block: Configuration.() -> Unit): AuthPlugin {
            val config = Configuration().apply(block)
            return AuthPlugin(config.tokenManager, config.onRefreshFailed)
        }

        override fun install(plugin: AuthPlugin, scope: HttpClient) {
            val platform = getPlatform()
            val refreshMutex = Mutex()
            var isRefreshing = false

            scope.plugin(HttpSend).intercept { request ->
                // Add token to request
                plugin.tokenManager.getToken()?.let { token ->
                    request.headers[HttpHeaders.Authorization] = "Bearer $token"
                }

                val originalCall = execute(request)

                // If 401, try to refresh token and retry
                if (originalCall.response.status == HttpStatusCode.Unauthorized) {
                    platform.log("⚠️ 401 Unauthorized - attempting token refresh", "AuthPlugin", LogSeverity.WARN)

                    val refreshSuccess = refreshMutex.withLock {
                        if (isRefreshing) {
                            // Another request is already refreshing, wait and use the new token
                            true
                        } else {
                            isRefreshing = true
                            try {
                                plugin.refreshToken()
                            } finally {
                                isRefreshing = false
                            }
                        }
                    }

                    if (refreshSuccess) {
                        platform.log("✅ Token refreshed - retrying request", "AuthPlugin", LogSeverity.INFO)
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
            platform.log("❌ Refresh token expired", "AuthPlugin", LogSeverity.ERROR)
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

            val response: HttpResponse = client.post("${org.fruex.beerwall.BuildKonfig.BASE_URL}/mobile/auth/refreshToken") {
                contentType(ContentType.Application.Json)
                setBody(org.fruex.beerwall.remote.dto.auth.RefreshTokenRequest(refreshToken = refreshTokenValue))
            }

            client.close()

            if (response.status == HttpStatusCode.OK) {
                val refreshResponse: org.fruex.beerwall.remote.dto.auth.RefreshTokenResponse = response.body()

                tokenManager.saveTokens(
                    tokens = org.fruex.beerwall.auth.AuthTokens(
                        token = refreshResponse.token,
                        tokenExpires = refreshResponse.tokenExpires,
                        refreshToken = refreshResponse.refreshToken,
                        refreshTokenExpires = refreshResponse.refreshTokenExpires,
                        firstName = null,
                        lastName = null
                    )
                )
                platform.log("✅ Token refreshed successfully", "AuthPlugin", LogSeverity.INFO)
                true
            } else {
                platform.log("❌ Token refresh failed: ${response.status}", "AuthPlugin", LogSeverity.ERROR)
                false
            }
        } catch (e: Exception) {
            platform.log("❌ Token refresh exception: ${e.message}", "AuthPlugin", LogSeverity.ERROR)
            false
        }
    }
}
