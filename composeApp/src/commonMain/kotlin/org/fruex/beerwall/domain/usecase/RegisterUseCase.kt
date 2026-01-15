package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.AuthRepository

/**
 * Przypadek użycia do rejestracji nowego użytkownika.
 *
 * @property authRepository Repozytorium autoryzacji.
 */
class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Wykonuje rejestrację.
     *
     * @param email Adres email użytkownika.
     * @param password Hasło użytkownika.
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.register(email, password)
    }
}
