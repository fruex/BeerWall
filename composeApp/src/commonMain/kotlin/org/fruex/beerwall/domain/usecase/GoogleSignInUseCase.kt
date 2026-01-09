package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.auth.GoogleUser
import org.fruex.beerwall.domain.repository.AuthRepository

class GoogleSignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<GoogleUser> {
        return authRepository.googleSignIn(idToken)
    }
}
