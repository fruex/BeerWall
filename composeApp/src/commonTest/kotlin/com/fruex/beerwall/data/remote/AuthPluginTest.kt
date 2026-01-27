package com.fruex.beerwall.data.remote

import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.Platform
import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.data.remote.api.AuthApiClient
import com.fruex.beerwall.data.remote.dto.auth.RefreshTokenResponse
import com.fruex.beerwall.data.remote.dto.auth.RefreshTokenEnvelope
import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.domain.model.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthPluginTest {

    private val EXPIRED_ACCESS_TOKEN = "expired_access_token"
    private val VALID_REFRESH_TOKEN = "valid_refresh_token"

    private val mockTokenManager = object : TokenManager {
        override suspend fun saveTokens(tokens: AuthTokens) {}
        override suspend fun getToken(): String? = EXPIRED_ACCESS_TOKEN
        override suspend fun getRefreshToken(): String? = VALID_REFRESH_TOKEN
        override suspend fun isTokenExpired(): Boolean = true
        override suspend fun isRefreshTokenExpired(): Boolean = false
        override suspend fun getTokenExpires(): Long? = 0
        override suspend fun getRefreshTokenExpires(): Long? = 0
        override suspend fun clearTokens() {}
        override suspend fun getUserProfile(): UserProfile? = null
        override suspend fun isFirstLaunch(): Boolean = false
        override suspend fun markFirstLaunchSeen() {}
    }

    private fun createMockClientWithAuthPlugin(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
    ): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler(handler)
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            // Install AuthPlugin explicitly as HttpClientFactory does
            install(AuthPlugin) {
                this.tokenManager = mockTokenManager
            }
        }
    }

    @Test
    fun `refreshToken request should NOT be intercepted by AuthPlugin`() = runTest {
        var capturedAuthorizationHeader: String? = null

        val client = createMockClientWithAuthPlugin { request ->
            capturedAuthorizationHeader = request.headers[HttpHeaders.Authorization]

            respond("{}", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }

        // Simulate what AuthApiClient.refreshToken does:
        // calling the refresh endpoint manually with the refresh token in the header
        val refreshTokenUrl = "${com.fruex.beerwall.BuildKonfig.BASE_URL}/${ApiRoutes.Auth.REFRESH_TOKEN}"

        client.get(refreshTokenUrl) {
            header(HttpHeaders.Authorization, "Bearer $VALID_REFRESH_TOKEN")
        }

        // Verify that the Authorization header matches the manually set Refresh Token
        // If AuthPlugin is buggy, it will overwrite it with EXPIRED_ACCESS_TOKEN
        assertEquals("Bearer $VALID_REFRESH_TOKEN", capturedAuthorizationHeader,
            "AuthPlugin overwrote the Refresh Token with the Access Token!")
    }

    @Test
    fun `external domain request should NOT have Authorization header attached`() = runTest {
        var capturedAuthorizationHeader: String? = null

        val client = createMockClientWithAuthPlugin { request ->
            capturedAuthorizationHeader = request.headers[HttpHeaders.Authorization]
            respond("{}", HttpStatusCode.OK)
        }

        // Request to an external domain
        client.get("https://google.com/search")

        assertEquals(null, capturedAuthorizationHeader, "Auth token leaked to external domain!")
    }

    @Test
    fun `api domain request should have Authorization header attached`() = runTest {
        var capturedAuthorizationHeader: String? = null

        val client = createMockClientWithAuthPlugin { request ->
            capturedAuthorizationHeader = request.headers[HttpHeaders.Authorization]
            respond("{}", HttpStatusCode.OK)
        }

        // Request to API domain (but not a public endpoint)
        // Ensure path does NOT match public endpoints
        val apiUrl = "${com.fruex.beerwall.BuildKonfig.BASE_URL}/mobile/users/profile"
        client.get(apiUrl)

        assertEquals("Bearer $EXPIRED_ACCESS_TOKEN", capturedAuthorizationHeader, "Auth token missing for API domain!")
    }
}
