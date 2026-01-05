package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.domain.model.Transaction

interface TransactionRepository {
    suspend fun getTransactions(): Result<List<Transaction>>
}
