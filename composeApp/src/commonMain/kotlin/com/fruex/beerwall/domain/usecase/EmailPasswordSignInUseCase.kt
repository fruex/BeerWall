package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.domain.repository.AuthRepository

/**
 * Przypadek użycia do logowania użytkownika za pomocą adresu email i hasła.
 *
 * @property authRepository Repozytorium autoryzacji.
 */
class EmailPasswordSignInUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Wykonuje logowanie.
     *
     * @param email Adres email użytkownika.
     * @param password Hasło użytkownika.
     * @return [Result] zawierający [AuthTokens] w przypadku sukcesu lub błąd.
     */
    suspend operator fun invoke(email: String, password: String): Result<AuthTokens> {
        return authRepository.emailPasswordSignIn(email, password)
    }
}
