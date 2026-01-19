package com.fruex.beerwall.data.repository

import com.fruex.beerwall.data.mapper.toDomain
import com.fruex.beerwall.data.remote.api.HistoryApiClient
import com.fruex.beerwall.domain.model.Transaction
import com.fruex.beerwall.domain.repository.TransactionRepository

/**
 * Implementacja repozytorium transakcji.
 *
 * @property historyApiClient Klient API dla operacji na historii transakcji.
 */
class TransactionRepositoryImpl(
    private val historyApiClient: HistoryApiClient
) : TransactionRepository {

    override suspend fun getTransactions(): Result<List<Transaction>> {
        return historyApiClient.getHistory().map { it.toDomain() }
    }
}
