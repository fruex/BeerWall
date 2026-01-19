package com.fruex.beerwall.fakes

import com.fruex.beerwall.domain.model.Transaction
import com.fruex.beerwall.domain.repository.TransactionRepository

class FakeTransactionRepository : TransactionRepository {
    var shouldFail = false
    var failureMessage = "Błąd pobierania historii"

    val fakeTransactions = mutableListOf<Transaction>()

    override suspend fun getTransactions(): Result<List<Transaction>> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(fakeTransactions)
    }
}
