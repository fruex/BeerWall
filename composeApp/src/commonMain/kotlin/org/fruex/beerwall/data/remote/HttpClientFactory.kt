package org.fruex.beerwall.data.remote

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.getPlatform
import org.fruex.beerwall.log

/**
 * Fabryka do tworzenia skonfigurowanych klientów HTTP.
 * Centralizuje konfigurację klienta HTTP dla wszystkich klientów API.
 */
object HttpClientFactory {

    private val platform = getPlatform()

    val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    /**
     * Tworzy i konfiguruje instancję HttpClient.
     * Konfiguracja obejmuje negocjację treści (JSON) oraz logowanie.
     *
     * @return Skonfigurowana instancja [HttpClient].
     */
    fun create(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    platform.log(message, "KtorClient", LogSeverity.DEBUG)
                }
            }
            level = LogLevel.ALL
            filter { request ->
                request.url.host.contains("igibeer")
            }
        }
    }
}
