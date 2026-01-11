package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.model.Transaction
import org.fruex.beerwall.domain.repository.TransactionRepository

/**
 * Przypadek użycia do pobierania historii transakcji.
 *
 * @property transactionRepository Repozytorium transakcji.
 */
class GetTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    /**
     * Pobiera historię transakcji.
     * @return Result z listą obiektów [Transaction].
     */
    suspend operator fun invoke(): Result<List<Transaction>> {
        return transactionRepository.getTransactions()
    }
}
