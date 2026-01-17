package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.mapper.toDomain
import org.fruex.beerwall.data.remote.api.HistoryApiClient
import org.fruex.beerwall.domain.model.Transaction
import org.fruex.beerwall.domain.repository.TransactionRepository

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
