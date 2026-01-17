package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.remote.api.SupportApiClient
import org.fruex.beerwall.domain.repository.SupportRepository

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
