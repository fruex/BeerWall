package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.CardRepository

class DeleteCardUseCase(
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(guid: String): Result<Unit> {
        return cardRepository.deleteCard(guid)
    }
}
