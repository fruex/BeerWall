package com.fruex.beerwall.data.remote

import io.ktor.client.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.fruex.beerwall.BuildKonfig
import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.getPlatform
import com.fruex.beerwall.log

/**
 * Factory for creating configured HTTP clients.
 * Centralizes HTTP client configuration for all API clients.
 */
object HttpClientFactory {

    private val platform = getPlatform()

    val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    fun create(
        tokenManager: TokenManager? = null,
        onUnauthorized: (suspend () -> Unit)? = null
    ): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }

        // Auth plugin - automatically refreshes token on 401
        if (tokenManager != null) {
            install(AuthPlugin) {
                this.tokenManager = tokenManager
                this.onRefreshFailed = onUnauthorized ?: {}
            }
        }

        if (BuildKonfig.DEBUG) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        val sanitizedMessage = LogSanitizer.sanitize(message)
                        platform.log(sanitizedMessage, "KtorClient", LogSeverity.DEBUG)
                    }
                }
                level = LogLevel.ALL
                filter { request ->
                    request.url.host.contains("igibeer")
                }
                sanitizeHeader { header -> header == io.ktor.http.HttpHeaders.Authorization }
            }
        }
    }
}
