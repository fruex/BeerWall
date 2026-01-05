package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.model.Card
import org.fruex.beerwall.domain.repository.CardRepository

class GetCardsUseCase(
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(): Result<List<Card>> {
        return cardRepository.getCards()
    }
}
