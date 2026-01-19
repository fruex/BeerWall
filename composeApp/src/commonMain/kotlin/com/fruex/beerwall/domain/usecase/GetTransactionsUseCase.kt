package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.model.Transaction
import com.fruex.beerwall.domain.repository.TransactionRepository

/**
 * Przypadek użycia do pobierania historii transakcji użytkownika.
 *
 * @property transactionRepository Repozytorium transakcji.
 */
class GetTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    /**
     * Pobiera historię transakcji.
     *
     * @return [Result] zawierający listę obiektów [Transaction] lub błąd.
     */
    suspend operator fun invoke(): Result<List<Transaction>> {
        return transactionRepository.getTransactions()
    }
}
