package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.remote.dto.operators.PaymentOperatorResponse

interface BalanceRepository {
    suspend fun getBalances(): Result<List<Balance>>
    suspend fun topUp(premisesId: Int, paymentMethodId: Int, balance: Double): Result<Unit>
    suspend fun getPaymentOperators(): Result<List<PaymentOperatorResponse>>
}
