package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.domain.model.Card

interface CardRepository {
    suspend fun getCards(): Result<List<Card>>
    suspend fun toggleCardStatus(cardId: String, isActive: Boolean): Result<Boolean>
}
