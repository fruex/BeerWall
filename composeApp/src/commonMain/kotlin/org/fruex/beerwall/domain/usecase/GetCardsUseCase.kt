package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.model.Card
import org.fruex.beerwall.domain.repository.CardRepository

/**
 * Przypadek użycia do pobierania listy kart użytkownika.
 *
 * @property cardRepository Repozytorium kart.
 */
class GetCardsUseCase(
    private val cardRepository: CardRepository
) {
    /**
     * Pobiera karty.
     *
     * @return [Result] zawierający listę obiektów [Card] lub błąd.
     */
    suspend operator fun invoke(): Result<List<Card>> {
        return cardRepository.getCards()
    }
}
