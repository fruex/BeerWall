package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.AuthRepository
import com.fruex.beerwall.domain.model.UserProfile

/**
 * Przypadek użycia do pobierania profilu zalogowanego użytkownika.
 */
class GetUserProfileUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): UserProfile? {
        return authRepository.getUserProfile()
    }
}
