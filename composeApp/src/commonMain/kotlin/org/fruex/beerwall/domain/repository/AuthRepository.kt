package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.auth.AuthTokens

interface AuthRepository {
    suspend fun googleSignIn(idToken: String): Result<AuthTokens>
    suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens>
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun refreshToken(): Result<AuthTokens>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun logout()
}
