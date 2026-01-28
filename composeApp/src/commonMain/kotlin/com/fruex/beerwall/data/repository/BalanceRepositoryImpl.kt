package com.fruex.beerwall.data.repository

import com.fruex.beerwall.data.mapper.toDomain
import com.fruex.beerwall.data.remote.api.BalanceApiClient
import com.fruex.beerwall.domain.model.Balance
import com.fruex.beerwall.domain.model.GdprClause
import com.fruex.beerwall.domain.repository.BalanceRepository
import com.fruex.beerwall.domain.model.PaymentOperator

/**
 * Implementacja repozytorium sald.
 *
 * @property balanceApiClient Klient API dla operacji na saldach.
 */
class BalanceRepositoryImpl(
    private val balanceApiClient: BalanceApiClient
) : BalanceRepository {

    override suspend fun getBalances(): Result<List<Balance>> {
        return balanceApiClient.getBalance().map { it.toDomain() }
    }

    override suspend fun getGdprClause(): Result<GdprClause> {
        return balanceApiClient.getGdprClause().map { it.toDomain() }
    }

    override suspend fun topUp(premisesId: Int, paymentMethodId: Int, balance: Double, authorizationCode: String?): Result<Unit> {
        return balanceApiClient.topUp(premisesId, paymentMethodId, balance, authorizationCode)
    }

    override suspend fun getPaymentOperators(): Result<List<PaymentOperator>> {
        return balanceApiClient.getPaymentOperators().map { it.toDomain() }
    }
}
