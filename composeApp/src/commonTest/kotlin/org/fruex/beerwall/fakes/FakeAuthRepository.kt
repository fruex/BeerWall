package org.fruex.beerwall.fakes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.domain.repository.AuthRepository
import org.fruex.beerwall.ui.models.UserProfile

class FakeAuthRepository : AuthRepository {
    var shouldFail = false
    var failureMessage = "Błąd autoryzacji"

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile("", "", "?"))
    override val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

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
        _isLoggedIn.value = true
        updateProfile()
        return Result.success(fakeTokens)
    }

    override suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        _isLoggedIn.value = true
        updateProfile()
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
        return _isLoggedIn.value
    }

    override suspend fun logout() {
        _isLoggedIn.value = false
        _userProfile.value = UserProfile("", "", "?")
    }

    // Metoda pomocnicza do ustawiania stanu w testach
    fun setLoggedIn(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
        if (loggedIn) updateProfile()
    }

    private fun updateProfile() {
        _userProfile.value = UserProfile(
            name = "${fakeTokens.firstName} ${fakeTokens.lastName}",
            email = "",
            initials = "JK"
        )
    }
}
