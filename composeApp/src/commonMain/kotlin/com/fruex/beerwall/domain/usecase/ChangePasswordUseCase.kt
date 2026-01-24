package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.AuthRepository

/**
 * UseCase do zmiany hasła przez zalogowanego użytkownika.
 */
class ChangePasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(oldPassword: String, newPassword: String): Result<Unit> {
        return authRepository.changePassword(oldPassword, newPassword)
    }
}
