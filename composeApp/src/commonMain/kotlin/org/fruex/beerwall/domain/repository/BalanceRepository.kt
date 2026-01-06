package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.remote.dto.operators.PaymentOperator

interface BalanceRepository {
    suspend fun getBalances(): Result<List<Balance>>
    suspend fun topUp(paymentMethodId: Int, balance: Double): Result<Double>
    suspend fun getPaymentOperators(): Result<List<PaymentOperator>>
}
