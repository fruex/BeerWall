package com.fruex.beerwall.data.remote.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.Platform
import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.data.remote.ApiRoutes
import com.fruex.beerwall.data.remote.BaseApiClient
import com.fruex.beerwall.log
import com.fruex.beerwall.getPlatform
import com.fruex.beerwall.data.remote.dto.user.FeedbackRequest
import io.ktor.client.HttpClient

/**
 * Klient API do obsługi wsparcia użytkownika.
 * Obsługuje wysyłanie wiadomości do supportu.
 */
class SupportApiClient(
    tokenManager: TokenManager,
    onUnauthorized: (suspend () -> Unit)? = null,
    httpClient: HttpClient? = null,
    platform: Platform = getPlatform()
) : BaseApiClient(tokenManager, onUnauthorized, httpClient, platform) {

    /**
     * Wysyła wiadomość od użytkownika do supportu.
     *
     * @param message Treść wiadomości.
     * @return Result pusty w przypadku sukcesu.
     */
    suspend fun sendMessage(message: String): Result<Unit> = try {
        platform.log("📤 Send Feedback Request", this, LogSeverity.INFO)
        val response = client.post("$baseUrl/${ApiRoutes.Users.FEEDBACK}") {
            addAuthToken()
            contentType(ContentType.Application.Json)
            setBody(FeedbackRequest(message))
        }

        when (response.status) {
            HttpStatusCode.NoContent -> {
                platform.log("✅ Send Feedback Success", this, LogSeverity.INFO)
                Result.success(Unit)
            }
            HttpStatusCode.Unauthorized -> {
                platform.log("❌ Send Feedback Unauthorized", this, LogSeverity.ERROR)
                Result.failure(Exception("Unauthorized"))
            }
            else -> {
                val bodyText = response.bodyAsText()
                platform.log("❌ Send Feedback Error: ${response.status} - $bodyText", this, LogSeverity.ERROR)
                Result.failure(Exception(if (bodyText.isNotBlank()) bodyText else "Error sending feedback: ${response.status}"))
            }
        }
    } catch (e: Exception) {
        platform.log("❌ Send Feedback Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }
}
