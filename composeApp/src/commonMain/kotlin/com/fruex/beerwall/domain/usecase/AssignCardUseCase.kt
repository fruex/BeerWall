package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.CardRepository

/**
 * Przypadek użycia odpowiedzialny za przypisanie nowej karty do użytkownika.
 *
 * @property cardRepository Repozytorium kart.
 */
class AssignCardUseCase(
    private val cardRepository: CardRepository
) {
    /**
     * Wykonuje operację przypisania karty.
     *
     * @param guid Unikalny identyfikator karty (GUID).
     * @param description Opis karty.
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend operator fun invoke(guid: String, description: String): Result<Unit> {
        return cardRepository.assignCard(guid, description)
    }
}
