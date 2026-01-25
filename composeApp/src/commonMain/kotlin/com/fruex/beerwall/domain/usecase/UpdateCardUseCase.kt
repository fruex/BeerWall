package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.CardRepository

/**
 * Przypadek użycia do aktualizacji danych karty (status aktywności, opis).
 *
 * @property cardRepository Repozytorium kart.
 */
class UpdateCardUseCase(
    private val cardRepository: CardRepository
) {
    /**
     * Aktualizuje dane karty.
     *
     * @param cardId Identyfikator karty.
     * @param description Opis karty.
     * @param isActive Status aktywności (true = aktywna).
     * @return [Result] pusty w przypadku sukcesu lub błąd.
     */
    suspend operator fun invoke(cardId: String, description: String, isActive: Boolean): Result<Unit> {
        return cardRepository.updateCard(cardId, description, isActive)
    }
}
