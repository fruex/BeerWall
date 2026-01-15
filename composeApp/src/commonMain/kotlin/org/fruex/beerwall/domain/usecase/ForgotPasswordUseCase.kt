package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.AuthRepository

/**
 * Przypadek użycia do obsługi procedury zapomnianego hasła.
 *
 * @property authRepository Repozytorium autoryzacji.
 */
class ForgotPasswordUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Inicjuje procedurę odzyskiwania hasła.
     *
     * @param email Adres email użytkownika.
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend operator fun invoke(email: String): Result<Unit> {
        return authRepository.forgotPassword(email)
    }
}
