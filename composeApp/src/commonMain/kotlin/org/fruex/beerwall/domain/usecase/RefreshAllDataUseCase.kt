package org.fruex.beerwall.domain.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.model.Card
import org.fruex.beerwall.domain.model.Transaction

data class AllData(
    val balances: List<Balance>? = null,
    val cards: List<Card>? = null,
    val transactions: List<Transaction>? = null,
    val loyaltyPoints: Int? = null
)

class RefreshAllDataUseCase(
    private val getBalancesUseCase: GetBalancesUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getLoyaltyPointsUseCase: GetLoyaltyPointsUseCase
) {
    suspend operator fun invoke(): AllData = coroutineScope {
        val balancesDeferred = async { getBalancesUseCase() }
        val cardsDeferred = async { getCardsUseCase() }
        val transactionsDeferred = async { getTransactionsUseCase() }
        val loyaltyPointsDeferred = async { getLoyaltyPointsUseCase() }

        AllData(
            balances = balancesDeferred.await().getOrNull(),
            cards = cardsDeferred.await().getOrNull(),
            transactions = transactionsDeferred.await().getOrNull(),
            loyaltyPoints = loyaltyPointsDeferred.await().getOrNull()
        )
    }
}
