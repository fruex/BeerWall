package org.fruex.beerwall.data.repository

import kotlinx.datetime.Clock
import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.auth.decodeTokenPayload
import org.fruex.beerwall.data.remote.api.AuthApiClient
import org.fruex.beerwall.domain.repository.AuthRepository
import org.fruex.beerwall.getPlatform
import org.fruex.beerwall.log

/**
 * Implementacja repozytorium autoryzacji.
 *
 * @property authApiClient Klient API dla operacji autentykacji.
 * @property tokenManager Menedżer tokenów do przechowywania danych sesji.
 */
class AuthRepositoryImpl(
    private val authApiClient: AuthApiClient,
    private val tokenManager: TokenManager
) : AuthRepository {
    private val platform = getPlatform()

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

        val now = Clock.System.now().epochSeconds

        // Heurystyka: Jeśli czas wygaśnięcia jest mały (< 1 000 000 000, czyli ok. rok 2001),
        // traktujemy go jako czas trwania (TTL) w sekundach i dodajemy do obecnego czasu.
        // W przeciwnym razie traktujemy go jako timestamp.
        val actualTokenExpires = if (tokenExpires < 1_000_000_000) {
            now + tokenExpires
        } else {
            tokenExpires
        }

        val actualRefreshTokenExpires = if (refreshTokenExpires < 1_000_000_000) {
            now + refreshTokenExpires
        } else {
            refreshTokenExpires
        }

        return AuthTokens(
            token = token,
            tokenExpires = actualTokenExpires,
            refreshToken = refreshToken,
            refreshTokenExpires = actualRefreshTokenExpires,
            firstName = firstName,
            lastName = lastName
        )
    }

    override suspend fun googleSignIn(idToken: String): Result<AuthTokens> {
        return authApiClient.googleSignIn(idToken).mapCatching { response ->
            platform.log("🔐 Google Login success, saving tokens...", this, LogSeverity.INFO)
            
            val tokens = createAuthTokens(
                token = response.token,
                tokenExpires = response.tokenExpires,
                refreshToken = response.refreshToken,
                refreshTokenExpires = response.refreshTokenExpires
            )
            
            tokenManager.saveTokens(tokens)
            platform.log("✅ Tokens saved", this, LogSeverity.DEBUG)
            tokens
        }
    }

    override suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens> {
        return authApiClient.emailPasswordSignIn(email, password).mapCatching { response ->
            platform.log("🔐 Email Login success, saving tokens...", this, LogSeverity.INFO)
            
            val tokens = createAuthTokens(
                token = response.token,
                tokenExpires = response.tokenExpires,
                refreshToken = response.refreshToken,
                refreshTokenExpires = response.refreshTokenExpires
            )
            
            tokenManager.saveTokens(tokens)
            platform.log("✅ Tokens saved", this, LogSeverity.DEBUG)
            tokens
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

        return true
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
    }
}
