package org.fruex.beerwall.data.remote.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.BaseApiClient
import org.fruex.beerwall.log
import org.fruex.beerwall.data.remote.dto.cards.*

/**
 * Klient API do obsługi operacji na kartach.
 * Obsługuje pobieranie kart, przypisywanie nowych oraz zarządzanie statusem (aktywacja/dezaktywacja).
 */
class CardsApiClient(tokenManager: TokenManager) : BaseApiClient(tokenManager) {

    /**
     * Pobiera listę kart przypisanych do użytkownika.
     *
     * @return Result zawierający listę [CardResponse] lub błąd.
     */
    suspend fun getCards(): Result<List<CardResponse>> =
        safeCallWithAuth<GetCardsEnvelope, List<CardResponse>> {
            get("$baseUrl/mobile/cards") {
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
        val response = client.put("$baseUrl/mobile/cards/assign") {
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
        // Uwaga: Endpoint w kodzie to /mobile/cards, metoda PUT. Swagger mówi o /mobile/cards (PUT) dla update'u.
        // Zakładamy, że CardUpdateMobileRequest w Swaggerze odpowiada logice tutaj.
        // Jednak tutaj używamy CardActivationRequest. Sprawdzić zgodność z DTO.
        val response = client.put("$baseUrl/mobile/cards") {
            addAuthToken()
            contentType(ContentType.Application.Json)
            setBody(CardActivationRequest(cardId, isActive))
        }

        if (response.status == HttpStatusCode.NoContent) {
            // Swagger zwraca 204 No Content dla PUT /mobile/cards, więc nie ma body.
            // Zwracamy sztuczny obiekt odpowiedzi, ponieważ metoda oczekuje CardActivationResponse.
            // TODO: Dostosować return type metody lub sprawdzić czy backend nie zaczął zwracać body.
            Result.success(CardActivationResponse(cardId, isActive, "Status updated"))
        } else {
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
        val response = client.delete("$baseUrl/mobile/cards") {
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
