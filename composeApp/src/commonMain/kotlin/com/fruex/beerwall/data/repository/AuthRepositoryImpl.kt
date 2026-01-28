package com.fruex.beerwall.data.repository

import kotlinx.coroutines.flow.Flow
import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.domain.model.SessionStatus
import com.fruex.beerwall.auth.ISessionManager
import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.domain.model.UserProfile
import com.fruex.beerwall.data.local.ensureTimestamp
import com.fruex.beerwall.data.local.decodeTokenPayload
import com.fruex.beerwall.data.remote.api.AuthApiClient
import com.fruex.beerwall.domain.exceptions.UnauthorizedException
import com.fruex.beerwall.domain.repository.AuthRepository
import com.fruex.beerwall.getPlatform
import com.fruex.beerwall.log

/**
 * Implementacja repozytorium autoryzacji.
 *
 * @property authApiClient Klient API dla operacji autentykacji.
 * @property tokenManager Menedżer tokenów do przechowywania danych sesji.
 * @property sessionManager Menedżer sesji do obserwowania stanu.
 */
class AuthRepositoryImpl(
    private val authApiClient: AuthApiClient,
    private val tokenManager: TokenManager,
    private val sessionManager: ISessionManager
) : AuthRepository {
    private val platform = getPlatform()

    override suspend fun checkSessionStatus(): SessionStatus {
        val refreshToken = tokenManager.getRefreshToken()

        // Brak tokenów - użytkownik niezalogowany
        if (refreshToken == null) {
            val isFirstLaunch = tokenManager.isFirstLaunch()
            return if (isFirstLaunch) {
                SessionStatus.FirstLaunch
            } else {
                SessionStatus.Guest
            }
        }

        // Sprawdź czy refresh token nie wygasł
        if (tokenManager.isRefreshTokenExpired()) {
            platform.log("Refresh token expired - session expired", this, LogSeverity.INFO)
            tokenManager.clearTokens()
            return SessionStatus.Expired
        }

        // Jeśli access token wygasł ale refresh token jest ważny, spróbuj odświeżyć
        if (tokenManager.isTokenExpired()) {
            val refreshResult = refreshToken()
            if (refreshResult.isSuccess) {
                sessionManager.setLoggedIn(true)
                return SessionStatus.Authenticated
            } else {
                val exception = refreshResult.exceptionOrNull()
                if (exception is UnauthorizedException) {
                    // Token unieważniony po stronie serwera
                    platform.log("Refresh token rejected: ${exception.message}", this, LogSeverity.WARN)
                    tokenManager.clearTokens()
                    return SessionStatus.Expired
                } else {
                    // Błąd sieci lub inny błąd - nie wylogowuj użytkownika
                    platform.log("Token refresh failed (network/other): ${exception?.message}", this, LogSeverity.WARN)
                    // Zakładamy, że sesja jest ważna (offline), choć token wygasł.
                    // Aplikacja powinna obsłużyć błędy sieci przy kolejnych żądaniach.
                    sessionManager.setLoggedIn(true)
                    return SessionStatus.Authenticated
                }
            }
        }

        // Wszystko ok
        sessionManager.setLoggedIn(true)
        return SessionStatus.Authenticated
    }

    override fun observeSessionState(): Flow<Boolean> = sessionManager.isUserLoggedIn

    private fun createAuthTokens(
        token: String,
        tokenExpires: Long,
        refreshToken: String,
        refreshTokenExpires: Long
    ): AuthTokens {
        // Dekodujemy token raz przy zapisie, aby nie robić tego przy każdym odczycie
        val payload = decodeTokenPayload(token)
        val firstName = payload["firstName"]
        val lastName = payload["lastName"]

        return AuthTokens(
            token = token,
            tokenExpires = ensureTimestamp(tokenExpires),
            refreshToken = refreshToken,
            refreshTokenExpires = ensureTimestamp(refreshTokenExpires),
            firstName = firstName,
            lastName = lastName
        )
    }

    /**
     * Przetwarza odpowiedź z logowania i zapisuje tokeny.
     * Wspólna logika dla różnych metod logowania.
     */
    private suspend fun processLoginResponse(
        token: String,
        tokenExpires: Long,
        refreshToken: String,
        refreshTokenExpires: Long,
        loginMethod: String
    ): AuthTokens {
        platform.log("$loginMethod success, saving tokens...", this, LogSeverity.INFO)

        val tokens = createAuthTokens(
            token = token,
            tokenExpires = tokenExpires,
            refreshToken = refreshToken,
            refreshTokenExpires = refreshTokenExpires
        )

        tokenManager.saveTokens(tokens)
        sessionManager.setLoggedIn(true)
        platform.log("Tokens saved", this, LogSeverity.SUCCESS)

        return tokens
    }

    override suspend fun googleSignIn(idToken: String): Result<AuthTokens> {
        return authApiClient.googleSignIn(idToken).mapCatching { response ->
            processLoginResponse(
                token = response.token,
                tokenExpires = response.tokenExpires,
                refreshToken = response.refreshToken,
                refreshTokenExpires = response.refreshTokenExpires,
                loginMethod = "Google Login"
            )
        }
    }

    override suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens> {
        return authApiClient.emailPasswordSignIn(email, password).mapCatching { response ->
            processLoginResponse(
                token = response.token,
                tokenExpires = response.tokenExpires,
                refreshToken = response.refreshToken,
                refreshTokenExpires = response.refreshTokenExpires,
                loginMethod = "Email Login"
            )
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return authApiClient.register(email, password)
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return authApiClient.forgotPassword(email)
    }

    override suspend fun resetPassword(email: String, resetCode: String, newPassword: String): Result<Unit> {
        return authApiClient.resetPassword(email, resetCode, newPassword)
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return authApiClient.changePassword(oldPassword, newPassword)
    }

    override suspend fun refreshToken(): Result<AuthTokens> {
        val currentRefreshToken = tokenManager.getRefreshToken()
            ?: return Result.failure(Exception("No refresh token available"))

        // Sprawdź czy refresh token nie wygasł
        if (tokenManager.isRefreshTokenExpired()) {
            tokenManager.clearTokens()
            return Result.failure(Exception("Refresh token expired"))
        }

        return authApiClient.refreshToken(currentRefreshToken).mapCatching { response ->
            val tokens = createAuthTokens(
                token = response.token,
                tokenExpires = response.tokenExpires,
                refreshToken = response.refreshToken,
                refreshTokenExpires = response.refreshTokenExpires
            )

            tokenManager.saveTokens(tokens)
            tokens
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        // Sprawdzamy czy tokeny istnieją
        if (tokenManager.getToken() == null || tokenManager.getRefreshToken() == null) {
            return false
        }

        // Jeśli oba tokeny wygasły, użytkownik nie jest zalogowany
        if (tokenManager.isTokenExpired() && tokenManager.isRefreshTokenExpired()) {
            tokenManager.clearTokens()
            return false
        }

        // Jeśli access token wygasł ale refresh token jest ważny, odśwież token
        if (tokenManager.isTokenExpired() && !tokenManager.isRefreshTokenExpired()) {
            return refreshToken().isSuccess
        }

        sessionManager.setLoggedIn(true)
        return true
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
        sessionManager.setLoggedIn(false)
    }

    override suspend fun getUserProfile(): UserProfile? {
        return tokenManager.getUserProfile()
    }

    override suspend fun markFirstLaunchSeen() {
        tokenManager.markFirstLaunchSeen()
    }
}
