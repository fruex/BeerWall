package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.mapper.toDomain
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.remote.dto.balance.TopUpResponseData
import org.fruex.beerwall.remote.dto.operators.PaymentOperator

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

    override suspend fun topUp(venueId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponseData> {
        // TODO: Mapowanie odpowiedzi (TopUpResponseData) na obiekt domenowy, aby uniezależnić warstwę domeny od DTO z remote.
        return dataSource.topUp(venueId, paymentMethodId, balance)
    }

    override suspend fun getPaymentOperators(): Result<List<PaymentOperator>> {
        // TODO: Mapowanie listy operatorów na obiekty domenowe.
        return dataSource.getPaymentOperators()
    }
}
