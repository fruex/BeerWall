package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.mapper.toDomain
import org.fruex.beerwall.data.mapper.toDomainOperators
import org.fruex.beerwall.data.remote.api.BalanceApiClient
import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.model.PaymentOperator
import org.fruex.beerwall.domain.repository.BalanceRepository

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

    override suspend fun topUp(premisesId: Int, paymentMethodId: Int, balance: Double): Result<Unit> {
        return balanceApiClient.topUp(premisesId, paymentMethodId, balance)
    }

    override suspend fun getPaymentOperators(): Result<List<PaymentOperator>> {
        return balanceApiClient.getPaymentOperators().map { it.toDomainOperators() }
    }
}
