package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.AuthRepository

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
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend operator fun invoke(email: String): Result<Unit> {
        return authRepository.resetPassword(email)
    }
}
