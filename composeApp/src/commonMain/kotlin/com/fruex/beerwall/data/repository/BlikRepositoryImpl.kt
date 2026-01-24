package com.fruex.beerwall.data.repository

import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.data.remote.ApiRoutes
import com.fruex.beerwall.data.remote.BaseApiClient
import com.fruex.beerwall.data.remote.dto.payments.BlikSocketMessage
import com.fruex.beerwall.data.remote.dto.payments.BlikStatus
import com.fruex.beerwall.domain.repository.BlikRepository
import com.fruex.beerwall.log
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

class BlikRepositoryImpl(
    tokenManager: TokenManager
) : BaseApiClient(tokenManager, null), BlikRepository {

    override fun connectToBlikSession(transactionId: String?): Flow<BlikStatus> = flow {
        val token = tokenManager.getToken()
        if (token == null) {
            platform.log("Cannot connect to BLIK session: No token", this, LogSeverity.ERROR)
            emit(BlikStatus.FAILURE)
            return@flow
        }

        // Konwersja URL HTTP na WS
        val wsBaseUrl = baseUrl
            .replace("https://", "wss://")
            .replace("http://", "ws://")

        val fullUrl = "$wsBaseUrl/${ApiRoutes.Payments.BLIK_WS}"

        try {
            platform.log("Connecting to BLIK WS: $fullUrl", this, LogSeverity.INFO)

            client.webSocket(
                urlString = fullUrl,
                request = {
                    header(HttpHeaders.Authorization, "Bearer $token")
                    if (transactionId != null) {
                        parameter("transactionId", transactionId)
                    }
                }
            ) {
                platform.log("Connected to BLIK WS", this, LogSeverity.INFO)
                emit(BlikStatus.PENDING)

                while (isActive) {
                    val frame = incoming.receive()
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        platform.log("BLIK WS Message: $text", this, LogSeverity.DEBUG)

                        try {
                            val message = json.decodeFromString<BlikSocketMessage>(text)
                            emit(message.status)

                            // Jeśli status jest końcowy, możemy zamknąć połączenie (opcjonalnie)
                            if (message.status == BlikStatus.SUCCESS ||
                                message.status == BlikStatus.FAILURE ||
                                message.status == BlikStatus.EXPIRED ||
                                message.status == BlikStatus.CANCELLED) {
                                // close() // Możemy zamknąć lub czekać aż serwer zamknie
                            }
                        } catch (e: Exception) {
                            platform.log("Error parsing BLIK message: ${e.message}", this, LogSeverity.ERROR)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            platform.log("BLIK WS Error: ${e.message}", this, LogSeverity.ERROR)
            emit(BlikStatus.FAILURE)
        }
    }
}
