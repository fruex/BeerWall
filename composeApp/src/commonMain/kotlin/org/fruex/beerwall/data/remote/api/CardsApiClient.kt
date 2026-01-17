package org.fruex.beerwall.data.remote.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.BaseApiClient
import org.fruex.beerwall.log
import org.fruex.beerwall.remote.dto.cards.*

/**
 * API client for card operations.
 * Handles card retrieval, status toggling, assignment, and deletion.
 */
class CardsApiClient(tokenManager: TokenManager) : BaseApiClient(tokenManager) {

    /**
     * Retrieves all cards associated with current user.
     */
    suspend fun getCards(): Result<List<CardResponse>> =
        safeCallWithAuth<GetCardsEnvelope, List<CardResponse>> {
            get("$baseUrl/mobile/cards") {
                addAuthToken()
            }.body()
        }

    /**
     * Toggles card status (active/inactive).
     */
    suspend fun toggleCardStatus(cardId: String, activate: Boolean): Result<CardActivationResponse> =
        safeCallWithAuth<CardActivationEnvelope, CardActivationResponse> {
            post("$baseUrl/mobile/cards/activation") {
                addAuthToken()
                contentType(ContentType.Application.Json)
                setBody(CardActivationRequest(cardId, activate))
            }.body()
        }

    /**
     * Assigns a new card to user with given description.
     */
    suspend fun assignCard(guid: String, description: String): Result<Unit> = try {
        platform.log("📤 Assign Card Request", this, LogSeverity.INFO)
        val response = client.put("$baseUrl/mobile/cards/assign") {
            addAuthToken()
            contentType(ContentType.Application.Json)
            setBody(AssignCardRequest(guid, description))
        }

        when (response.status) {
            HttpStatusCode.NoContent -> {
                platform.log("✅ Assign Card Success", this, LogSeverity.INFO)
                Result.success(Unit)
            }
            HttpStatusCode.Unauthorized -> {
                platform.log("❌ Assign Card Unauthorized", this, LogSeverity.ERROR)
                Result.failure(Exception("Unauthorized"))
            }
            HttpStatusCode.NotFound -> {
                platform.log("❌ Assign Card Not Found", this, LogSeverity.ERROR)
                Result.failure(Exception("Card not found"))
            }
            else -> {
                val bodyText = response.bodyAsText()
                platform.log("❌ Assign Card Error: ${response.status} - $bodyText", this, LogSeverity.ERROR)
                Result.failure(Exception("Error assigning card: ${response.status}"))
            }
        }
    } catch (e: Exception) {
        platform.log("❌ Assign Card Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }

    /**
     * Deletes card from user's account.
     */
    suspend fun deleteCard(guid: String): Result<Unit> = try {
        platform.log("📤 Delete Card Request", this, LogSeverity.INFO)
        val response = client.delete("$baseUrl/mobile/cards/delete") {
            addAuthToken()
            contentType(ContentType.Application.Json)
            parameter("guid", guid)
        }

        when (response.status) {
            HttpStatusCode.NoContent -> {
                platform.log("✅ Delete Card Success", this, LogSeverity.INFO)
                Result.success(Unit)
            }
            HttpStatusCode.Unauthorized -> {
                platform.log("❌ Delete Card Unauthorized", this, LogSeverity.ERROR)
                Result.failure(Exception("Unauthorized"))
            }
            HttpStatusCode.NotFound -> {
                platform.log("❌ Delete Card Not Found", this, LogSeverity.ERROR)
                Result.failure(Exception("Card not found"))
            }
            else -> {
                val bodyText = response.bodyAsText()
                platform.log("❌ Delete Card Error: ${response.status} - $bodyText", this, LogSeverity.ERROR)
                Result.failure(Exception("Error deleting card: ${response.status}"))
            }
        }
    } catch (e: Exception) {
        platform.log("❌ Delete Card Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }
}
