package org.fruex.beerwall.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokens(
    val token: String,
    val tokenExpires: Long,
    val refreshToken: String,
    val refreshTokenExpires: Long
)

interface TokenManager {
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun getToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun isTokenExpired(): Boolean
    suspend fun clearTokens()
}


expect class TokenManagerImpl : TokenManager {
    override suspend fun saveTokens(tokens: AuthTokens)
    override suspend fun getToken(): String?
    override suspend fun getRefreshToken(): String?
    override suspend fun isTokenExpired(): Boolean
    override suspend fun clearTokens()
}
