package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.CardRepository

class AssignCardUseCase(
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(guid: String, description: String): Result<Unit> {
        return cardRepository.assignCard(guid, description)
    }
}
