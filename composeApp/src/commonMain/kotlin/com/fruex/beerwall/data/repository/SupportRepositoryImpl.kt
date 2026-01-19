package com.fruex.beerwall.data.repository

import com.fruex.beerwall.data.remote.api.SupportApiClient
import com.fruex.beerwall.domain.repository.SupportRepository

/**
 * Implementacja repozytorium wsparcia.
 *
 * @property supportApiClient Klient API dla operacji wsparcia.
 */
class SupportRepositoryImpl(
    private val supportApiClient: SupportApiClient
) : SupportRepository {
    override suspend fun sendMessage(message: String): Result<Unit> =
        supportApiClient.sendMessage(message)
}
