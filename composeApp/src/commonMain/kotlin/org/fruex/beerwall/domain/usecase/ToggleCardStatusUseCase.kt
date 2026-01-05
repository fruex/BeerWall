package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.CardRepository

class ToggleCardStatusUseCase(
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(cardId: String, isActive: Boolean): Result<Boolean> {
        return cardRepository.toggleCardStatus(cardId, isActive)
    }
}
