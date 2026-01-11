package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.auth.GoogleUser

/**
 * Interfejs repozytorium odpowiedzialnego za autoryzację użytkownika.
 */
interface AuthRepository {
    /**
     * Loguje użytkownika za pomocą tokenu Google ID.
     * @param idToken Token tożsamości otrzymany od Google.
     * @return Result zawierający dane użytkownika Google w przypadku sukcesu.
     */
    suspend fun googleSignIn(idToken: String): Result<GoogleUser>

    /**
     * Loguje użytkownika za pomocą adresu email i hasła.
     * @param email Adres email użytkownika.
     * @param password Hasło użytkownika.
     * @return Result zawierający tokeny autoryzacyjne (access i refresh).
     */
    suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens>

    /**
     * Odświeża token dostępu (access token) używając refresh tokena.
     * @return Result z nową parą tokenów.
     */
    suspend fun refreshToken(): Result<AuthTokens>

    /**
     * Sprawdza, czy użytkownik jest obecnie zalogowany (czy posiada ważną sesję/tokeny).
     * @return true jeśli użytkownik jest zalogowany, false w przeciwnym razie.
     */
    suspend fun isUserLoggedIn(): Boolean

    /**
     * Wylogowuje użytkownika, czyszcząc lokalne dane sesji.
     */
    suspend fun logout()
}
