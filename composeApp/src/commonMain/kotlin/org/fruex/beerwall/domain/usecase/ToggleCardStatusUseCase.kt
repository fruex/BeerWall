package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.CardRepository

/**
 * Przypadek użycia do zmiany statusu aktywności karty (blokada/odblokowanie).
 *
 * @property cardRepository Repozytorium kart.
 */
class ToggleCardStatusUseCase(
    private val cardRepository: CardRepository
) {
    /**
     * Zmienia status karty.
     *
     * @param cardId Identyfikator karty.
     * @param isActive Nowy status aktywności (true = aktywna).
     * @return [Result] zawierający nowy status lub błąd.
     */
    suspend operator fun invoke(cardId: String, isActive: Boolean): Result<Boolean> {
        return cardRepository.toggleCardStatus(cardId, isActive)
    }
}
