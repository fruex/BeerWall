package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.mapper.toDomain
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.remote.dto.operators.PaymentOperator

class BalanceRepositoryImpl(
    private val dataSource: BeerWallDataSource
) : BalanceRepository {

    override suspend fun getBalances(): Result<List<Balance>> {
        return dataSource.getBalance().map { it.toDomain() }
    }

    override suspend fun topUp(paymentMethodId: Int, balance: Double): Result<Double> {
        return dataSource.topUp(paymentMethodId, balance).map { it.newBalance }
    }

    override suspend fun getPaymentOperators(): Result<List<PaymentOperator>> {
        return dataSource.getPaymentOperators()
    }
}
