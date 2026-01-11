package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.auth.GoogleUser

interface AuthRepository {
    suspend fun googleSignIn(idToken: String): Result<GoogleUser>
    suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens>
    suspend fun refreshToken(): Result<AuthTokens>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun logout()
}
