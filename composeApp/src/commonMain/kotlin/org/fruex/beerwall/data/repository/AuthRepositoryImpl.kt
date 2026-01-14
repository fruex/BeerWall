package org.fruex.beerwall.data.repository

import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.auth.decodeTokenPayload
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.repository.AuthRepository
import org.fruex.beerwall.getPlatform
import org.fruex.beerwall.log

class AuthRepositoryImpl(
    private val dataSource: BeerWallDataSource,
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

        return AuthTokens(
            token = token,
            tokenExpires = tokenExpires,
            refreshToken = refreshToken,
            refreshTokenExpires = refreshTokenExpires,
            firstName = firstName,
            lastName = lastName
        )
    }

    override suspend fun googleSignIn(idToken: String): Result<AuthTokens> {
        return dataSource.googleSignIn(idToken).mapCatching { response ->
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
        return dataSource.emailPasswordSignIn(email, password).mapCatching { response ->
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

    override suspend fun refreshToken(): Result<AuthTokens> {
        val currentRefreshToken = tokenManager.getRefreshToken()
            ?: return Result.failure(Exception("No refresh token available"))

        // Sprawdź czy refresh token nie wygasł
        if (tokenManager.isRefreshTokenExpired()) {
            tokenManager.clearTokens()
            return Result.failure(Exception("Refresh token expired"))
        }

        return dataSource.refreshToken(currentRefreshToken).mapCatching { response ->
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
        val token = tokenManager.getToken() ?: return false
        val refreshToken = tokenManager.getRefreshToken() ?: return false

        // Jeśli oba tokeny wygasły, użytkownik nie jest zalogowany
        if (tokenManager.isTokenExpired() && tokenManager.isRefreshTokenExpired()) {
            tokenManager.clearTokens()
            return false
        }

        return true
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
    }
}
