package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.ProfileRepository

class GetLoyaltyPointsUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return profileRepository.getLoyaltyPoints()
    }
}
