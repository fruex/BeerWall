package com.fruex.beerwall.domain.repository

import kotlinx.coroutines.flow.Flow
import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.domain.model.SessionStatus
import com.fruex.beerwall.domain.model.UserProfile

/**
 * Interfejs repozytorium do obsługi uwierzytelniania i zarządzania sesją użytkownika.
 */
interface AuthRepository {
    /**
     * Sprawdza szczegółowy status sesji użytkownika.
     *
     * @return [SessionStatus] określający czy użytkownik jest zalogowany, sesja wygasła, czy jest to pierwsze uruchomienie.
     */
    suspend fun checkSessionStatus(): SessionStatus

    /**
     * Obserwuje stan sesji użytkownika.
     *
     * @return [Flow] emitujący `true` jeśli użytkownik jest zalogowany, `false` w przeciwnym razie.
     */
    fun observeSessionState(): Flow<Boolean>

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
     * Zmienia hasło zalogowanego użytkownika.
     *
     * @param oldPassword Stare hasło.
     * @param newPassword Nowe hasło.
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>

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

    /**
     * Pobiera profil zalogowanego użytkownika.
     */
    suspend fun getUserProfile(): UserProfile?

    /**
     * Oznacza, że użytkownik widział już ekran pierwszego uruchomienia.
     */
    suspend fun markFirstLaunchSeen()
}
