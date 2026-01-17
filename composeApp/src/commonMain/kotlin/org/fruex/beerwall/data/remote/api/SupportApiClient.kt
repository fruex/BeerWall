package org.fruex.beerwall.data.remote.api

import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.BaseApiClient

/**
 * API client for support operations.
 * Handles user support message submissions.
 */
class SupportApiClient(tokenManager: TokenManager) : BaseApiClient(tokenManager) {

    /**
     * Sends support message from user.
     * Currently a mock implementation - simulates sending message with delay.
     */
    suspend fun sendMessage(message: String): Result<Unit> {
        // MOCK: Symulacja wysyłania wiadomości
        kotlinx.coroutines.delay(1000)
        return Result.success(Unit)
    }
}
