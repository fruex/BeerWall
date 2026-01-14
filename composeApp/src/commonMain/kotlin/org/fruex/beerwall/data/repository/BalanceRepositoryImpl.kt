package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.mapper.toDomain
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.remote.dto.balance.TopUpResponse
import org.fruex.beerwall.remote.dto.operators.PaymentOperatorResponse

/**
 * Implementacja repozytorium salda.
 *
 * @property dataSource Źródło danych (API BeerWall).
 */
class BalanceRepositoryImpl(
    private val dataSource: BeerWallDataSource
) : BalanceRepository {

    override suspend fun getBalances(): Result<List<Balance>> {
        return dataSource.getBalance().map { it.toDomain() }
    }

    override suspend fun topUp(premisesId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponse> {
        // TODO: Mapowanie odpowiedzi (TopUpResponse) na obiekt domenowy, aby uniezależnić warstwę domeny od DTO z remote.
        return dataSource.topUp(premisesId, paymentMethodId, balance)
    }

    override suspend fun getPaymentOperators(): Result<List<PaymentOperatorResponse>> {
        // TODO: Mapowanie listy operatorów na obiekty domenowe.
        return dataSource.getPaymentOperators()
    }
}
