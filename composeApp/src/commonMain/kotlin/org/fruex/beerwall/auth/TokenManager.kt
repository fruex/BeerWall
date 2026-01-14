package org.fruex.beerwall.auth

import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class AuthTokens(
    val token: String,
    val tokenExpires: Long,
    val refreshToken: String,
    val refreshTokenExpires: Long,
    val firstName: String? = null,
    val lastName: String? = null
)

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
        e.printStackTrace()
        return emptyMap()
    }
}
