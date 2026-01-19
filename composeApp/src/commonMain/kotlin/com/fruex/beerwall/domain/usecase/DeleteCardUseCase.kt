package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.CardRepository

/**
 * Przypadek użycia odpowiedzialny za usunięcie karty użytkownika.
 *
 * @property cardRepository Repozytorium kart.
 */
class DeleteCardUseCase(
    private val cardRepository: CardRepository
) {
    /**
     * Wykonuje operację usunięcia karty.
     *
     * @param guid Unikalny identyfikator karty (GUID) do usunięcia.
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend operator fun invoke(guid: String): Result<Unit> {
        return cardRepository.deleteCard(guid)
    }
}
