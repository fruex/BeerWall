package org.fruex.beerwall.data.remote.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.BaseApiClient
import org.fruex.beerwall.log
import org.fruex.beerwall.remote.dto.history.*

/**
 * API client for transaction history operations.
 * Handles retrieval of transaction records.
 */
class HistoryApiClient(tokenManager: TokenManager) : BaseApiClient(tokenManager) {

    /**
     * Retrieves transaction history for current user.
     */
    suspend fun getHistory(): Result<List<TransactionResponse>> =
        safeCallWithAuth<GetHistoryEnvelope, List<TransactionResponse>> {
            get("$baseUrl/mobile/users/history") {
                addAuthToken()
            }.body()
        }
}
