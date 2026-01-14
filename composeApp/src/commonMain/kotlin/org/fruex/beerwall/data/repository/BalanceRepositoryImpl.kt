package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.mapper.toDomain
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.remote.dto.balance.TopUpResponse
import org.fruex.beerwall.remote.dto.operators.PaymentOperatorResponse

class BalanceRepositoryImpl(
    private val dataSource: BeerWallDataSource
) : BalanceRepository {

    override suspend fun getBalances(): Result<List<Balance>> {
        return dataSource.getBalance().map { it.toDomain() }
    }

    override suspend fun topUp(premisesId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponse> {
        return dataSource.topUp(premisesId, paymentMethodId, balance)
    }

    override suspend fun getPaymentOperators(): Result<List<PaymentOperatorResponse>> {
        return dataSource.getPaymentOperators()
    }
}
