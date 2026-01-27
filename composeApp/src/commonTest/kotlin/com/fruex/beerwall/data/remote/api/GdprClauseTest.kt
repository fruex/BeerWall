package com.fruex.beerwall.data.remote.api

import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.Platform
import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.domain.model.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GdprClauseTest {

    private fun createMockClient(jsonResponse: String, status: HttpStatusCode = HttpStatusCode.OK): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = jsonResponse,
                        status = status,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    private val mockTokenManager = object : TokenManager {
        override suspend fun saveTokens(tokens: AuthTokens) {}
        override suspend fun getToken(): String? = "mock_token"
        override suspend fun getRefreshToken(): String? = null
        override suspend fun isTokenExpired(): Boolean = false
        override suspend fun isRefreshTokenExpired(): Boolean = false
        override suspend fun getTokenExpires(): Long? = null
        override suspend fun getRefreshTokenExpires(): Long? = null
        override suspend fun clearTokens() {}
        override suspend fun getUserProfile(): UserProfile? = null
        override suspend fun isFirstLaunch(): Boolean = false
        override suspend fun markFirstLaunchSeen() {}
    }

    private val fakePlatform = object : Platform {
        override val name: String = "TestPlatform"
        override fun log(message: String, tag: String, severity: LogSeverity) {
            println("[$severity] $tag: $message")
        }
    }

    @Test
    fun `test getGdprClause success`() = runTest {
        val jsonResponse = """
            {
                "data": {
                    "title": "Title",
                    "content": "Content",
                    "locale": "pl"
                },
                "error": null
            }
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val api = BalanceApiClient(mockTokenManager, httpClient = client, platform = fakePlatform)

        val result = api.getGdprClause()

        assertTrue(result.isSuccess, "Expected success but got failure: ${result.exceptionOrNull()}")
        val clause = result.getOrNull()
        assertEquals("Title", clause?.title)
        assertEquals("Content", clause?.content)
        assertEquals("pl", clause?.locale)
    }
}
