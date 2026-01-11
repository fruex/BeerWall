package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.domain.repository.AuthRepository

/**
 * Use case do logowania użytkownika przez email i hasło
 */
class EmailPasswordSignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthTokens> {
        return authRepository.emailPasswordSignIn(email, password)
    }
}
