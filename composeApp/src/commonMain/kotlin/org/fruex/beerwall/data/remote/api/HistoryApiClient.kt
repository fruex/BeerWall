package org.fruex.beerwall.data.remote.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.ApiRoutes
import org.fruex.beerwall.data.remote.BaseApiClient
import org.fruex.beerwall.remote.dto.history.GetHistoryEnvelope
import org.fruex.beerwall.remote.dto.history.TransactionResponse

/**
 * Klient API do obsługi operacji historii.
 * Obsługuje pobieranie historii transakcji użytkownika.
 */
class HistoryApiClient(tokenManager: TokenManager) : BaseApiClient(tokenManager) {

    /**
     * Pobiera historię transakcji dla obecnego użytkownika.
     *
     * @return Result zawierający listę [TransactionResponse] lub błąd.
     */
    suspend fun getHistory(): Result<List<TransactionResponse>> =
        safeCallWithAuth<GetHistoryEnvelope, List<TransactionResponse>> {
            get("$baseUrl/${ApiRoutes.Users.HISTORY}") {
                addAuthToken()
            }.body()
        }
}
