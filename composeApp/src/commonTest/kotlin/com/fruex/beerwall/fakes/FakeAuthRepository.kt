package com.fruex.beerwall.fakes

import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.domain.model.SessionStatus
import com.fruex.beerwall.domain.model.UserProfile
import com.fruex.beerwall.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeAuthRepository : AuthRepository {
    var shouldFail = false
    var failureMessage = "Błąd autoryzacji"
    var forcedSessionStatus: SessionStatus = SessionStatus.Guest

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

    override suspend fun checkSessionStatus(): SessionStatus {
        if (shouldFail) throw Exception(failureMessage)
        return forcedSessionStatus
    }

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

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
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

    override suspend fun getUserProfile(): UserProfile? {
        if (shouldFail) return null
        if (!_sessionState.value) return null
        return UserProfile(name = "${fakeTokens.firstName} ${fakeTokens.lastName}")
    }

    override suspend fun markFirstLaunchSeen() {
        // No-op for tests
    }

    // Metoda pomocnicza do ustawiania stanu w testach
    fun setLoggedIn(loggedIn: Boolean) {
        _sessionState.update { loggedIn }
    }

    override suspend fun getUserProfile(): UserProfile? {
        return null
    }

    override suspend fun markFirstLaunchSeen() {
        // no-op
    }
}
