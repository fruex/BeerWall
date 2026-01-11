package org.fruex.beerwall.data.repository

import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.auth.GoogleUser
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.repository.AuthRepository

/**
 * Implementacja repozytorium autoryzacji.
 * Odpowiada za komunikację ze źródłem danych (API) oraz zarządzanie lokalnym przechowywaniem tokenów.
 *
 * @property dataSource Źródło danych (API BeerWall).
 * @property tokenManager Menedżer tokenów do bezpiecznego przechowywania sesji lokalnie.
 */
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

            // TODO: Zwracany obiekt GoogleUser wydaje się być mapowany bezpośrednio z DTO odpowiedzi logowania.
            // Warto sprawdzić czy GoogleUser w domenie powinien zawierać tokeny techniczne, czy raczej dane profilowe.
            GoogleUser(
                idToken = response.token,
                tokenExpires = response.tokenExpires,
                refreshToken = response.refreshToken,
                refreshTokenExpires = response.refreshTokenExpires
            )
        }
    }

    override suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens> {
        return dataSource.emailPasswordSignIn(email, password).map { response ->
            // Zapisz tokeny do lokalnego storage
            val tokens = AuthTokens(
                token = response.tokenDto.token,
                tokenExpires = response.tokenDto.tokenExpires,
                refreshToken = response.tokenDto.refreshToken,
                refreshTokenExpires = response.tokenDto.refreshTokenExpires
            )
            tokenManager.saveTokens(tokens)
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

        return dataSource.refreshToken(currentRefreshToken).map { response ->
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
        // TODO: Logika 'jeśli OBA wygasły' może być zbyt luźna. Zazwyczaj jeśli Access Token wygasł, próbujemy go odświeżyć.
        // Tutaj zwracamy true nawet jeśli Access Token wygasł, ale Refresh Token jest ważny. To jest poprawne przy założeniu,
        // że mechanizm odświeżania zadziała przy najbliższym zapytaniu API.
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
