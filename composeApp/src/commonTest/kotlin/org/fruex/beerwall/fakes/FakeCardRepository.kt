package org.fruex.beerwall.fakes

import org.fruex.beerwall.domain.model.Card
import org.fruex.beerwall.domain.repository.CardRepository

class FakeCardRepository : CardRepository {
    var shouldFail = false
    var failureMessage = "Błąd operacji na kartach"

    val fakeCards = mutableListOf(
        Card(
            id = "card-1",
            name = "Moja Karta",
            isActive = true,
            isPhysical = true
        ),
        Card(
            id = "card-2",
            name = "Karta Zapasowa",
            isActive = false,
            isPhysical = false
        )
    )

    override suspend fun getCards(): Result<List<Card>> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(fakeCards)
    }

    override suspend fun toggleCardStatus(cardId: String, isActive: Boolean): Result<Boolean> {
        if (shouldFail) return Result.failure(Exception(failureMessage))

        val index = fakeCards.indexOfFirst { it.id == cardId }
        if (index != -1) {
            val current = fakeCards[index]
            fakeCards[index] = current.copy(isActive = isActive)
            return Result.success(isActive)
        }
        return Result.failure(Exception("Karta nie znaleziona"))
    }

    override suspend fun assignCard(guid: String, description: String): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        fakeCards.add(
            Card(
                id = guid,
                name = description,
                isActive = true,
                isPhysical = true
            )
        )
        return Result.success(Unit)
    }

    override suspend fun deleteCard(guid: String): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        val initialSize = fakeCards.size
        fakeCards.removeAll { it.id == guid }
        return if (fakeCards.size < initialSize) Result.success(Unit) else Result.failure(Exception("Karta nie znaleziona"))
    }
}
