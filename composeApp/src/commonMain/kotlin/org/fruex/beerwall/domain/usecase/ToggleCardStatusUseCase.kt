package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.CardRepository

/**
 * Przypadek użycia do zmiany statusu karty.
 *
 * @property cardRepository Repozytorium kart.
 */
class ToggleCardStatusUseCase(
    private val cardRepository: CardRepository
) {
    /**
     * Przełącza status karty.
     * @param cardId ID karty.
     * @param isActive Nowy status aktywności.
     * @return Result z nowym statusem (boolean).
     */
    suspend operator fun invoke(cardId: String, isActive: Boolean): Result<Boolean> {
        return cardRepository.toggleCardStatus(cardId, isActive)
    }
}
