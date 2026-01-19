package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}
