package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.mapper.toDomain
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.repository.BalanceRepository

class BalanceRepositoryImpl(
    private val dataSource: BeerWallDataSource
) : BalanceRepository {
    
    override suspend fun getBalances(): Result<List<Balance>> {
        return dataSource.getBalance().map { it.toDomain() }
    }

    override suspend fun topUp(amount: Double, venueName: String): Result<Double> {
        return dataSource.topUp(amount, venueName).map { it.newBalance }
    }
}
