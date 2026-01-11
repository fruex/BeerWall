package org.fruex.beerwall.auth

import kotlinx.serialization.Serializable

/**
 * Model przechowujący tokeny autoryzacyjne i informacje o ich wygaśnięciu.
 */
@Serializable
data class AuthTokens(
    val token: String,
    val tokenExpires: Long,
    val refreshToken: String,
    val refreshTokenExpires: Long
)

/**
 * Interfejs zarządzający przechowywaniem tokenów autoryzacyjnych.
 * Pozwala na zapisywanie, odczytywanie i sprawdzanie ważności tokenów w bezpieczny sposób.
 */
interface TokenManager {
    /**
     * Zapisuje komplet tokenów.
     */
    suspend fun saveTokens(tokens: AuthTokens)

    /**
     * Zwraca aktualny token dostępu (access token).
     */
    suspend fun getToken(): String?

    /**
     * Zwraca token odświeżania (refresh token).
     */
    suspend fun getRefreshToken(): String?

    /**
     * Sprawdza czy token dostępu wygasł.
     */
    suspend fun isTokenExpired(): Boolean

    /**
     * Sprawdza czy token odświeżania wygasł.
     */
    suspend fun isRefreshTokenExpired(): Boolean

    /**
     * Zwraca czas wygaśnięcia tokenu dostępu (timestamp).
     */
    suspend fun getTokenExpires(): Long?

    /**
     * Zwraca czas wygaśnięcia tokenu odświeżania (timestamp).
     */
    suspend fun getRefreshTokenExpires(): Long?

    /**
     * Usuwa wszystkie tokeny (wylogowanie).
     */
    suspend fun clearTokens()
}

/**
 * Oczekiwana implementacja platformowa TokenManager (Android/iOS).
 */
expect class TokenManagerImpl : TokenManager {
    override suspend fun saveTokens(tokens: AuthTokens)
    override suspend fun getToken(): String?
    override suspend fun getRefreshToken(): String?
    override suspend fun isTokenExpired(): Boolean
    override suspend fun isRefreshTokenExpired(): Boolean
    override suspend fun getTokenExpires(): Long?
    override suspend fun getRefreshTokenExpires(): Long?
    override suspend fun clearTokens()
}
