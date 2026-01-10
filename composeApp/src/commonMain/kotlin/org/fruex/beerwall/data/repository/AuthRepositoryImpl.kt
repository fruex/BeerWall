package org.fruex.beerwall.data.repository

import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.auth.GoogleUser
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val dataSource: BeerWallDataSource,
    private val tokenManager: TokenManager
) : AuthRepository {
    override suspend fun googleSignIn(idToken: String): Result<GoogleUser> {
        return dataSource.googleSignIn(idToken).map { response ->
            // Zapisz tokeny do lokalnego storage
            val tokens = AuthTokens(
                token = response.token,
                tokenExpires = response.tokenExpires,
                refreshToken = response.refreshToken,
                refreshTokenExpires = response.refreshTokenExpires
            )
            tokenManager.saveTokens(tokens)

            GoogleUser(
                idToken = response.token,
                tokenExpires = response.tokenExpires,
                refreshToken = response.refreshToken,
                refreshTokenExpires = response.refreshTokenExpires
            )
        }
    }
}
