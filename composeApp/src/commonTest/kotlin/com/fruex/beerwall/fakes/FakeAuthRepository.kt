package com.fruex.beerwall.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {
    var shouldFail = false
    var failureMessage = "Błąd autoryzacji"

    private val _sessionState = MutableStateFlow(false)
    private val fakeTokens = AuthTokens(
        token = "fake-token",
        tokenExpires = 3600L,
        refreshToken = "fake-refresh-token",
        refreshTokenExpires = 7200L,
        firstName = "Jan",
        lastName = "Kowalski"
    )

    override fun observeSessionState(): Flow<Boolean> = _sessionState.asStateFlow()

    override suspend fun googleSignIn(idToken: String): Result<AuthTokens> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        _sessionState.update { true }
        return Result.success(fakeTokens)
    }

    override suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        _sessionState.update { true }
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

    override suspend fun changePassword(newPassword: String): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(Unit)
    }

    override suspend fun refreshToken(): Result<AuthTokens> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(fakeTokens)
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return _sessionState.value
    }

    override suspend fun logout() {
        _sessionState.update { false }
    }

    // Metoda pomocnicza do ustawiania stanu w testach
    fun setLoggedIn(loggedIn: Boolean) {
        _sessionState.update { loggedIn }
    }
}
