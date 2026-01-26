package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.AuthRepository

/**
 * Przypadek użycia do oznaczania, że użytkownik widział już ekran powitalny (pierwsze uruchomienie).
 */
class MarkFirstLaunchSeenUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.markFirstLaunchSeen()
    }
}
