package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.repository.SupportRepository

class SupportRepositoryImpl(
    private val dataSource: BeerWallDataSource
) : SupportRepository {
    override suspend fun sendMessage(message: String): Result<Unit> =
        dataSource.sendMessage(message)
}
