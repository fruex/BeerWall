package com.fruex.beerwall.data.remote.api

import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.Platform
import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.ui.models.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.request.HttpResponseData
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.close
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.core.readText
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BlikPaymentTest {

    private fun createMockClient(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): HttpClient {
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
    }

    private val fakePlatform = object : Platform {
        override val name: String = "TestPlatform"
        override fun log(message: String, tag: String, severity: LogSeverity) {
            println("[$severity] $tag: $message")
        }
    }

    private suspend fun HttpRequestData.getBodyAsString(): String {
        return try {
            when (val content = body) {
                is OutgoingContent.ByteArrayContent -> content.bytes().decodeToString()
                is OutgoingContent.WriteChannelContent -> {
                    val channel = ByteChannel()
                    content.writeTo(channel)
                    channel.flush()
                    channel.close()
                    channel.readRemaining().readText()
                }
                is OutgoingContent.NoContent -> ""
                else -> ""
            }
        } catch (e: Exception) {
            "ERROR_READING_BODY: ${e.message}"
        }
    }

    @Test
    fun `test blik 111 111 - payment success`() = runTest {
        val client = createMockClient { request ->
            val body = request.getBodyAsString()
            if (body.contains("111111")) {
                respond("", HttpStatusCode.NoContent)
            } else {
                respond("Bad Request: $body", HttpStatusCode.BadRequest)
            }
        }
        val api = BalanceApiClient(mockTokenManager, httpClient = client, platform = fakePlatform)

        val result = api.topUp(1, 1, 10.0, "111111")

        assertTrue(result.isSuccess, "Result failed: ${result.exceptionOrNull()?.message}")
    }

    @Test
    fun `test blik 222 222 - payment timeout`() = runTest {
        val client = createMockClient { request ->
            val body = request.getBodyAsString()
            if (body.contains("222222")) {
                respond("Upłynął czas oczekiwania", HttpStatusCode.RequestTimeout)
            } else {
                respond("Bad Request", HttpStatusCode.BadRequest)
            }
        }
        val api = BalanceApiClient(mockTokenManager, httpClient = client, platform = fakePlatform)

        val result = api.topUp(1, 1, 10.0, "222222")

        assertTrue(result.isFailure)
        assertEquals("Upłynął czas oczekiwania", result.exceptionOrNull()?.message)
    }

    @Test
    fun `test blik 333 333 - payment rejected (auth failed)`() = runTest {
        val client = createMockClient { request ->
            val body = request.getBodyAsString()
            if (body.contains("333333")) {
                respond("Płatność odrzucona", HttpStatusCode.BadRequest)
            } else {
                respond("Bad Request", HttpStatusCode.BadRequest)
            }
        }
        val api = BalanceApiClient(mockTokenManager, httpClient = client, platform = fakePlatform)

        val result = api.topUp(1, 1, 10.0, "333333")

        assertTrue(result.isFailure)
        assertEquals("Płatność odrzucona", result.exceptionOrNull()?.message)
    }

    @Test
    fun `test blik 333 334 - payment expired`() = runTest {
        val client = createMockClient { request ->
            val body = request.getBodyAsString()
            if (body.contains("333334")) {
                respond("Płatność wygasła", HttpStatusCode.BadRequest)
            } else {
                respond("Bad Request", HttpStatusCode.BadRequest)
            }
        }
        val api = BalanceApiClient(mockTokenManager, httpClient = client, platform = fakePlatform)

        val result = api.topUp(1, 1, 10.0, "333334")

        assertTrue(result.isFailure)
        assertEquals("Płatność wygasła", result.exceptionOrNull()?.message)
    }

    @Test
    fun `test blik 333 335 - payment rejected (code used)`() = runTest {
        val client = createMockClient { request ->
            val body = request.getBodyAsString()
            if (body.contains("333335")) {
                respond("Płatność odrzucona", HttpStatusCode.BadRequest)
            } else {
                respond("Bad Request", HttpStatusCode.BadRequest)
            }
        }
        val api = BalanceApiClient(mockTokenManager, httpClient = client, platform = fakePlatform)

        val result = api.topUp(1, 1, 10.0, "333335")

        assertTrue(result.isFailure)
        assertEquals("Płatność odrzucona", result.exceptionOrNull()?.message)
    }

    @Test
    fun `test blik 333 336 - payment error (other)`() = runTest {
        val client = createMockClient { request ->
            val body = request.getBodyAsString()
            if (body.contains("333336")) {
                respond("Błąd płatności", HttpStatusCode.InternalServerError)
            } else {
                respond("Bad Request", HttpStatusCode.BadRequest)
            }
        }
        val api = BalanceApiClient(mockTokenManager, httpClient = client, platform = fakePlatform)

        val result = api.topUp(1, 1, 10.0, "333336")

        assertTrue(result.isFailure)
        assertEquals("Błąd płatności", result.exceptionOrNull()?.message)
    }

    @Test
    fun `test blik 444 444 - payment error after pending`() = runTest {
        val client = createMockClient { request ->
            val body = request.getBodyAsString()
            if (body.contains("444444")) {
                // Backend waits 20s then returns Error
                respond("Błąd płatności", HttpStatusCode.InternalServerError)
            } else {
                respond("Bad Request", HttpStatusCode.BadRequest)
            }
        }
        val api = BalanceApiClient(mockTokenManager, httpClient = client, platform = fakePlatform)

        val result = api.topUp(1, 1, 10.0, "444444")

        assertTrue(result.isFailure)
        assertEquals("Błąd płatności", result.exceptionOrNull()?.message)
    }
}
