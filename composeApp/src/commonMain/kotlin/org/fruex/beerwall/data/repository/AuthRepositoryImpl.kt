package org.fruex.beerwall.data.repository

import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.auth.GoogleUser
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.repository.AuthRepository
import org.fruex.beerwall.getPlatform
import org.fruex.beerwall.log

class AuthRepositoryImpl(
    private val dataSource: BeerWallDataSource,
    private val tokenManager: TokenManager
) : AuthRepository {
    private val platform = getPlatform()

    override suspend fun googleSignIn(idToken: String): Result<GoogleUser> {
        return dataSource.googleSignIn(idToken).mapCatching { response ->
            platform.log("🔐 Google Login success, saving tokens...", this, LogSeverity.INFO)
            // Zapisz tokeny do lokalnego storage
            val tokens = AuthTokens(
                token = response.token,
                tokenExpires = response.tokenExpires,
                refreshToken = response.refreshToken,
                refreshTokenExpires = response.refreshTokenExpires
            )
            tokenManager.saveTokens(tokens)
            platform.log("✅ Tokens saved", this, LogSeverity.DEBUG)

            GoogleUser(
                idToken = response.token,
                tokenExpires = response.tokenExpires,
                refreshToken = response.refreshToken,
                refreshTokenExpires = response.refreshTokenExpires
            )
        }
    }

    override suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens> {
        return dataSource.emailPasswordSignIn(email, password).mapCatching { response ->
            platform.log("🔐 Email Login success, saving tokens...", this, LogSeverity.INFO)
            // Zapisz tokeny do lokalnego storage
            val tokens = AuthTokens(
                token = response.tokenDto.token,
                tokenExpires = response.tokenDto.tokenExpires,
                refreshToken = response.tokenDto.refreshToken,
                refreshTokenExpires = response.tokenDto.refreshTokenExpires
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
            val tokens = AuthTokens(
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
