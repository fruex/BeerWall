package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.auth.AuthTokens
// TODO: `AuthTokens` pochodzi z pakietu `auth`, który może nie być częścią warstwy domeny. Należy rozważyć przeniesienie modelu tokenów do `domain/model`.

/**
 * Interfejs repozytorium do obsługi uwierzytelniania i zarządzania sesją użytkownika.
 */
interface AuthRepository {
    /**
     * Loguje użytkownika przy użyciu tokena Google.
     *
     * @param idToken Token tożsamości otrzymany od Google.
     * @return [Result] zawierający [AuthTokens] w przypadku sukcesu lub błąd.
     */
    suspend fun googleSignIn(idToken: String): Result<AuthTokens>

    /**
     * Loguje użytkownika przy użyciu adresu email i hasła.
     *
     * @param email Adres email użytkownika.
     * @param password Hasło użytkownika.
     * @return [Result] zawierający [AuthTokens] w przypadku sukcesu lub błąd.
     */
    suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens>

    /**
     * Rejestruje nowego użytkownika.
     *
     * @param email Adres email użytkownika.
     * @param password Hasło użytkownika.
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend fun register(email: String, password: String): Result<Unit>

    /**
     * Inicjuje procedurę przypomnienia hasła.
     *
     * @param email Adres email użytkownika.
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend fun forgotPassword(email: String): Result<Unit>

    /**
     * Resetuje hasło użytkownika.
     *
     * @param email Adres email użytkownika.
     * @param resetCode Kod resetujący hasło.
     * @param newPassword Nowe hasło.
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend fun resetPassword(email: String, resetCode: String, newPassword: String): Result<Unit>

    /**
     * Odświeża tokeny sesji.
     *
     * @return [Result] zawierający nowe [AuthTokens] w przypadku sukcesu lub błąd.
     */
    suspend fun refreshToken(): Result<AuthTokens>

    /**
     * Sprawdza, czy użytkownik jest obecnie zalogowany.
     *
     * @return `true` jeśli użytkownik jest zalogowany, `false` w przeciwnym razie.
     */
    suspend fun isUserLoggedIn(): Boolean

    /**
     * Wylogowuje użytkownika, czyszcząc dane sesji.
     */
    suspend fun logout()
}
