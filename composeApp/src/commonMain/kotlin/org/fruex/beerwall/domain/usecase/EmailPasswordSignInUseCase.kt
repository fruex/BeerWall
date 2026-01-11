package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.domain.repository.AuthRepository

/**
 * Przypadek użycia do logowania użytkownika przez email i hasło.
 *
 * @property authRepository Repozytorium autoryzacji.
 */
class EmailPasswordSignInUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Loguje użytkownika.
     *
     * @param email Email użytkownika.
     * @param password Hasło użytkownika.
     * @return Result zawierający tokeny autoryzacyjne.
     */
    suspend operator fun invoke(email: String, password: String): Result<AuthTokens> {
        return authRepository.emailPasswordSignIn(email, password)
    }
}
