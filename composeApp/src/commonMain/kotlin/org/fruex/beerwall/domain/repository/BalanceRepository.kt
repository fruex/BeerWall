package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.remote.dto.balance.TopUpResponseData
import org.fruex.beerwall.remote.dto.operators.PaymentOperator

interface BalanceRepository {
    suspend fun getBalances(): Result<List<Balance>>
    suspend fun topUp(venueId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponseData>
    suspend fun getPaymentOperators(): Result<List<PaymentOperator>>
}
