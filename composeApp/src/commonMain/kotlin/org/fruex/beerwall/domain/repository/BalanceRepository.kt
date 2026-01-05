package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.domain.model.Balance

interface BalanceRepository {
    suspend fun getBalances(): Result<List<Balance>>
    suspend fun topUp(amount: Double, venueName: String): Result<Double>
}
