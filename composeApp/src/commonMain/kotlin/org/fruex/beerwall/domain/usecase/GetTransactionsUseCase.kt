package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.model.Transaction
import org.fruex.beerwall.domain.repository.TransactionRepository

class GetTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(): Result<List<Transaction>> {
        return transactionRepository.getTransactions()
    }
}
