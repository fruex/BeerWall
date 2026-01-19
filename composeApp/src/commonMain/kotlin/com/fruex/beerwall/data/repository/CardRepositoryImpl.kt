package com.fruex.beerwall.data.repository

import com.fruex.beerwall.data.mapper.toDomain
import com.fruex.beerwall.data.remote.api.CardsApiClient
import com.fruex.beerwall.domain.model.Card
import com.fruex.beerwall.domain.repository.CardRepository

/**
 * Implementacja repozytorium kart.
 *
 * @property cardsApiClient Klient API dla operacji na kartach.
 */
class CardRepositoryImpl(
    private val cardsApiClient: CardsApiClient
) : CardRepository {

    override suspend fun getCards(): Result<List<Card>> {
        return cardsApiClient.getCards().map { it.toDomain() }
    }

    override suspend fun toggleCardStatus(cardId: String, isActive: Boolean): Result<Boolean> {
        return cardsApiClient.setCardStatus(cardId, isActive).map { it.isActive }
    }

    override suspend fun assignCard(guid: String, description: String): Result<Unit> {
        return cardsApiClient.assignCard(guid, description)
    }

    override suspend fun deleteCard(guid: String): Result<Unit> {
        return cardsApiClient.deleteCard(guid)
    }
}
