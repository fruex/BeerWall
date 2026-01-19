package com.fruex.beerwall.data.remote.api

import com.fruex.beerwall.auth.TokenManager
import com.fruex.beerwall.data.remote.BaseApiClient

/**
 * Klient API do obsługi wsparcia użytkownika.
 * Obsługuje wysyłanie wiadomości do supportu.
 */
class SupportApiClient(tokenManager: TokenManager) : BaseApiClient(tokenManager) {

    /**
     * Wysyła wiadomość od użytkownika do supportu.
     * Obecnie jest to implementacja mockowa - symuluje wysyłanie z opóźnieniem.
     *
     * @param message Treść wiadomości.
     * @return Result pusty w przypadku sukcesu.
     */
    suspend fun sendMessage(message: String): Result<Unit> {
        // MOCK: Symulacja wysyłania wiadomości
        kotlinx.coroutines.delay(1000)
        return Result.success(Unit)
    }
}
