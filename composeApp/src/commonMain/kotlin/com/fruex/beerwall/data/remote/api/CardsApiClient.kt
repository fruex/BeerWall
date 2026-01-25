package com.fruex.beerwall.data.remote.api

import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.data.remote.ApiRoutes
import com.fruex.beerwall.data.remote.BaseApiClient
import com.fruex.beerwall.data.remote.dto.cards.*
import com.fruex.beerwall.log
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Klient API do obsługi operacji na kartach.
 * Obsługuje pobieranie kart, przypisywanie nowych oraz zarządzanie statusem (aktywacja/dezaktywacja).
 */
class CardsApiClient(
    tokenManager: TokenManager,
    onUnauthorized: (suspend () -> Unit)? = null
) : BaseApiClient(tokenManager, onUnauthorized) {

    /**
     * Pobiera listę kart przypisanych do użytkownika.
     *
     * @return Result zawierający listę [CardResponse] lub błąd.
     */
    suspend fun getCards(): Result<List<CardResponse>> =
        safeCallWithAuth<GetCardsEnvelope, List<CardResponse>> {
            get("$baseUrl/${ApiRoutes.Cards.CARDS}") {
                addAuthToken()
            }.body()
        }

    /**
     * Przypisuje nową kartę do użytkownika.
     *
     * @param guid GUID karty.
     * @param description Opcjonalny opis karty.
     * @return Result pusty w przypadku sukcesu lub błąd.
     */
    suspend fun assignCard(guid: String, description: String): Result<Unit> = try {
        val response = client.put("$baseUrl/${ApiRoutes.Cards.ASSIGN}") {
            addAuthToken()
            contentType(ContentType.Application.Json)
            setBody(AssignCardRequest(guid, description))
        }

        if (response.status == HttpStatusCode.NoContent) {
            Result.success(Unit)
        } else {
            val bodyText = response.bodyAsText()
            platform.log("❌ Assign Card Error: ${response.status} - $bodyText", this, LogSeverity.ERROR)
            Result.failure(Exception("Failed to assign card"))
        }
    } catch (e: Exception) {
        platform.log("❌ Assign Card Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }

    /**
     * Aktualizuje dane karty (status, opis).
     *
     * @param cardId ID karty (GUID).
     * @param description Opis karty.
     * @param isActive Nowy status aktywności (true = aktywna).
     * @return Result pusty w przypadku sukcesu lub błąd.
     */
    suspend fun updateCard(cardId: String, description: String, isActive: Boolean): Result<Unit> = try {
        val response = client.put("$baseUrl/${ApiRoutes.Cards.CARDS}") {
            addAuthToken()
            contentType(ContentType.Application.Json)
            setBody(UpdateCardRequest(guid = cardId, isActive = isActive, description = description))
        }

        if (response.status == HttpStatusCode.NoContent) {
            Result.success(Unit)
        } else {
            val bodyText = response.bodyAsText()
            platform.log("❌ Update Card Error: ${response.status} - $bodyText", this, LogSeverity.ERROR)
            Result.failure(Exception("Failed to update card: ${response.status}"))
        }
    } catch (e: Exception) {
        platform.log("❌ Update Card Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }

    /**
     * Usuwa kartę użytkownika.
     *
     * @param cardId ID karty (GUID).
     * @return Result pusty w przypadku sukcesu lub błąd.
     */
    suspend fun deleteCard(cardId: String): Result<Unit> = try {
        val response = client.delete("$baseUrl/${ApiRoutes.Cards.CARDS}") {
            addAuthToken()
            parameter("guid", cardId)
        }

        if (response.status == HttpStatusCode.NoContent) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to delete card: ${response.status}"))
        }
    } catch (e: Exception) {
        platform.log("❌ Delete Card Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }
}
