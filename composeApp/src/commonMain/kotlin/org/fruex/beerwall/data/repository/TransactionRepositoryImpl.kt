package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.mapper.toDomain
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.model.Transaction
import org.fruex.beerwall.domain.repository.TransactionRepository

class TransactionRepositoryImpl(
    private val dataSource: BeerWallDataSource
) : TransactionRepository {
    
    override suspend fun getTransactions(): Result<List<Transaction>> {
        return dataSource.getHistory().map { it.toDomain() }
    }
}
