package org.fruex.beerwall.fakes

import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {
    var shouldFail = false
    var failureMessage = "Błąd autoryzacji"

    private var isLoggedIn = false
    private val fakeTokens = AuthTokens(
        token = "fake-token",
        tokenExpires = 3600L,
        refreshToken = "fake-refresh-token",
        refreshTokenExpires = 7200L,
        firstName = "Jan",
        lastName = "Kowalski"
    )

    override suspend fun googleSignIn(idToken: String): Result<AuthTokens> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        isLoggedIn = true
        return Result.success(fakeTokens)
    }

    override suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        isLoggedIn = true
        return Result.success(fakeTokens)
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(Unit)
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(Unit)
    }

    override suspend fun resetPassword(email: String, resetCode: String, newPassword: String): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(Unit)
    }

    override suspend fun refreshToken(): Result<AuthTokens> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(fakeTokens)
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return isLoggedIn
    }

    override suspend fun logout() {
        isLoggedIn = false
    }

    // Metoda pomocnicza do ustawiania stanu w testach
    fun setLoggedIn(loggedIn: Boolean) {
        this.isLoggedIn = loggedIn
    }
}
