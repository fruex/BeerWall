package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.model.Card
import org.fruex.beerwall.domain.repository.CardRepository

/**
 * Przypadek użycia do pobierania kart użytkownika.
 *
 * @property cardRepository Repozytorium kart.
 */
class GetCardsUseCase(
    private val cardRepository: CardRepository
) {
    /**
     * Pobiera listę kart.
     * @return Result z listą obiektów [Card].
     */
    suspend operator fun invoke(): Result<List<Card>> {
        return cardRepository.getCards()
    }
}
