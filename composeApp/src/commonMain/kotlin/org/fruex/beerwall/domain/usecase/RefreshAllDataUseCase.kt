package org.fruex.beerwall.domain.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.model.Card
import org.fruex.beerwall.domain.model.Transaction

/**
 * Model danych zwracanych przez RefreshAllDataUseCase
 * Wszystkie pola są nullable - błędy w pojedynczych requestach nie blokują całej operacji
 */
data class AllData(
    val balances: List<Balance>? = null,
    val cards: List<Card>? = null,
    val transactions: List<Transaction>? = null,
    val loyaltyPoints: Int? = null
)

/**
 * Use case do równoległego pobierania wszystkich danych aplikacji
 *
 * Wykonuje równolegle następujące operacje:
 * - Pobieranie sald z wszystkich miejsc
 * - Pobieranie listy kart użytkownika
 * - Pobieranie historii transakcji
 * - Pobieranie punktów lojalnościowych
 *
 * Błędy w pojedynczych requestach nie blokują całej operacji - zwracane są wszystkie
 * pomyślnie pobrane dane, a błędne pola pozostają null
 */
class RefreshAllDataUseCase(
    private val getBalancesUseCase: GetBalancesUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
) {
    suspend operator fun invoke(): AllData = coroutineScope {
        val balancesDeferred = async { getBalancesUseCase() }
        val cardsDeferred = async { getCardsUseCase() }
        val transactionsDeferred = async { getTransactionsUseCase() }

        AllData(
            balances = balancesDeferred.await().getOrNull(),
            cards = cardsDeferred.await().getOrNull(),
            transactions = transactionsDeferred.await().getOrNull(),
        )
    }
}
