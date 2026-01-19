package com.fruex.beerwall.fakes

import com.fruex.beerwall.domain.model.Balance
import com.fruex.beerwall.domain.repository.BalanceRepository
import com.fruex.beerwall.domain.model.PaymentOperator

class FakeBalanceRepository : BalanceRepository {
    var shouldFail = false
    var failureMessage = "Błąd pobierania salda"

    val fakeBalances = mutableListOf(
        Balance(
            premisesId = 1,
            premisesName = "Pub Testowy",
            balance = 50.0,
            loyaltyPoints = 100
        )
    )

    override suspend fun getBalances(): Result<List<Balance>> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(fakeBalances)
    }

    override suspend fun topUp(premisesId: Int, paymentMethodId: Int, balance: Double): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        // Symulacja aktualizacji lokalnego stanu
        val index = fakeBalances.indexOfFirst { it.premisesId == premisesId }
        if (index != -1) {
            val current = fakeBalances[index]
            fakeBalances[index] = current.copy(balance = current.balance + balance)
        }
        return Result.success(Unit)
    }

    override suspend fun getPaymentOperators(): Result<List<PaymentOperator>> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(emptyList())
    }
}
