package com.fruex.beerwall.auth

import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.fruex.beerwall.getPlatform
import com.fruex.beerwall.LogSeverity

/**
 * Model tokenów autoryzacyjnych aplikacji.
 *
 * @property token Token dostępu (JWT).
 * @property tokenExpires Czas wygaśnięcia tokenu dostępu.
 * @property refreshToken Token odświeżania.
 * @property refreshTokenExpires Czas wygaśnięcia tokenu odświeżania.
 * @property firstName Imię użytkownika (wyciągnięte z tokenu).
 * @property lastName Nazwisko użytkownika (wyciągnięte z tokenu).
 */
@Serializable
data class AuthTokens(
    val token: String,
    val tokenExpires: Long,
    val refreshToken: String,
    val refreshTokenExpires: Long,
    val firstName: String? = null,
    val lastName: String? = null
)

/**
 * Interfejs menedżera tokenów.
 * Odpowiada za bezpieczne przechowywanie i odczytywanie tokenów sesji.
 */
interface TokenManager {
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun getToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun isTokenExpired(): Boolean
    suspend fun isRefreshTokenExpired(): Boolean
    suspend fun getTokenExpires(): Long?
    suspend fun getRefreshTokenExpires(): Long?
    suspend fun clearTokens()
    suspend fun getUserName(): String?
}

// TODO: `TokenManager` jest interfejsem zdefiniowanym w `auth`, ale jego implementacja (`TokenManagerImpl`) oraz samo użycie sugeruje, że jest to Lokalne Źródło Danych (Local Data Source). Zgodnie z Clean Architecture, powinien znajdować się w warstwie `data` (np. `data/local` lub `data/repository`).

/**
 * Oczekiwana implementacja platformowa menedżera tokenów.
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
    override suspend fun getUserName(): String?
}

/**
 * Zwraca obecny czas w sekundach od początku epoki Unix.
 */
expect fun currentTimeSeconds(): Long

/**
 * Funkcja pomocnicza do upewnienia się, że czas wygaśnięcia jest absolutnym znacznikiem czasu.
 * Jeśli wartość jest mniejsza niż 1 miliard, traktujemy ją jako interwał w sekundach i dodajemy obecny czas.
 */
fun ensureTimestamp(value: Long): Long {
    val result = if (value < 1000000000L) {
        currentTimeSeconds() + value
    } else {
        value
    }
    
    // Loguj tylko jeśli wartość jest podejrzanie mała (np. 0) lub interwał
    if (value < 1000000000L) {
        getPlatform().log(
            "ensureTimestamp: input=$value, now=${currentTimeSeconds()}, result=$result",
            "TokenManager",
            LogSeverity.DEBUG
        )
    }
    
    return result
}

/**
 * Funkcja pomocnicza do dekodowania payloadu tokenu JWT.
 *
 * @param token Token JWT.
 * @return Mapa klucz-wartość z payloadu.
 */
@OptIn(ExperimentalEncodingApi::class)
fun decodeTokenPayload(token: String): Map<String, String> {
    try {
        val parts = token.split(".")
        if (parts.size < 2) return emptyMap()

        var payload = parts[1]
            .replace('-', '+')
            .replace('_', '/')

        when (payload.length % 4) {
            2 -> payload += "=="
            3 -> payload += "="
        }

        val decodedPayload = Base64.decode(payload).decodeToString()
        
        // Prosty parser JSON dla płaskiej struktury
        val result = mutableMapOf<String, String>()
        val regex = """"([^"]+)":\s*("([^"]*)"|(\d+))""".toRegex()
        
        regex.findAll(decodedPayload).forEach { match ->
            val key = match.groupValues[1]
            val value = match.groupValues[3].ifEmpty { match.groupValues[4] }
            result[key] = value
        }
        
        return result
    } catch (e: Exception) {
        getPlatform().log("Error decoding token payload: ${e.message}", "TokenManager", LogSeverity.ERROR)
        return emptyMap()
    }
}
