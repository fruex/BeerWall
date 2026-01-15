package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.register(email, password)
    }
}
