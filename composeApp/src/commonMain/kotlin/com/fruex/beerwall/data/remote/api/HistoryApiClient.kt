package com.fruex.beerwall.data.remote.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import com.fruex.beerwall.auth.TokenManager
import com.fruex.beerwall.data.remote.ApiRoutes
import com.fruex.beerwall.data.remote.BaseApiClient
import com.fruex.beerwall.data.remote.dto.history.GetHistoryEnvelope
import com.fruex.beerwall.data.remote.dto.history.TransactionResponse

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
