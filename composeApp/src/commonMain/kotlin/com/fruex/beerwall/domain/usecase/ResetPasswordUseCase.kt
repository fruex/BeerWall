package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.AuthRepository

/**
 * Przypadek użycia do resetowania hasła użytkownika.
 *
 * @property authRepository Repozytorium autoryzacji.
 */
class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Wykonuje reset hasła.
     *
     * @param email Adres email użytkownika.
     * @param resetCode Kod resetujący hasło.
     * @param newPassword Nowe hasło.
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend operator fun invoke(email: String, resetCode: String, newPassword: String): Result<Unit> {
        return authRepository.resetPassword(email, resetCode, newPassword)
    }
}
