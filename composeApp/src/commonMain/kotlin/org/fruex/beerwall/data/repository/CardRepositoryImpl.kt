package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.mapper.toDomain
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.model.Card
import org.fruex.beerwall.domain.repository.CardRepository

class CardRepositoryImpl(
    private val dataSource: BeerWallDataSource
) : CardRepository {
    
    override suspend fun getCards(): Result<List<Card>> {
        return dataSource.getCards().map { it.toDomain() }
    }

    override suspend fun toggleCardStatus(cardId: String, isActive: Boolean): Result<Boolean> {
        return dataSource.toggleCardStatus(cardId, isActive).map { it.isActive }
    }

    override suspend fun assignCard(guid: String, description: String): Result<Unit> {
        return dataSource.assignCard(guid, description)
    }

    override suspend fun deleteCard(guid: String): Result<Unit> {
        return dataSource.deleteCard(guid)
    }
}
