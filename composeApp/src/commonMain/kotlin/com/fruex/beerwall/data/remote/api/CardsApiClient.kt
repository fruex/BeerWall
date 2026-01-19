package com.fruex.beerwall.data.remote.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.auth.TokenManager
import com.fruex.beerwall.data.remote.ApiRoutes
import com.fruex.beerwall.data.remote.BaseApiClient
import com.fruex.beerwall.log
import com.fruex.beerwall.data.remote.dto.cards.*

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
     * Zmienia status aktywności karty.
     *
     * @param cardId ID karty (GUID).
     * @param isActive Nowy status aktywności (true = aktywna).
     * @return Result zawierający [CardActivationResponse] lub błąd.
     */
    suspend fun setCardStatus(cardId: String, isActive: Boolean): Result<CardActivationResponse> = try {
        val response = client.put("$baseUrl/${ApiRoutes.Cards.CARDS}") {
            addAuthToken()
            contentType(ContentType.Application.Json)
            setBody(CardActivationRequest(guid = cardId, isActive = isActive))
        }

        if (response.status == HttpStatusCode.NoContent) {
            // Swagger zwraca 204 No Content dla PUT /mobile/cards, więc nie ma body.
            // Zwracamy sztuczny obiekt odpowiedzi, ponieważ metoda oczekuje CardActivationResponse.
            Result.success(CardActivationResponse(cardId, isActive, "Status updated"))
        } else {
            val bodyText = response.bodyAsText()
            platform.log("❌ Set Card Status Error: ${response.status} - $bodyText", this, LogSeverity.ERROR)
            Result.failure(Exception("Failed to update card status: ${response.status}"))
        }
    } catch (e: Exception) {
        platform.log("❌ Set Card Status Exception: ${e.message}", this, LogSeverity.ERROR)
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
