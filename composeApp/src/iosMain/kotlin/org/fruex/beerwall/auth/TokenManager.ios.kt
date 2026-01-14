package org.fruex.beerwall.auth

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual class TokenManagerImpl : TokenManager {
    private var tokens: AuthTokens? = null
    
    actual override suspend fun saveTokens(tokens: AuthTokens) {
        this.tokens = tokens
        // TODO: Implement secure storage for iOS (Keychain)
    }

    actual override suspend fun getToken(): String? {
        // TODO: Implement secure storage for iOS (Keychain)
        return tokens?.token
    }

    actual override suspend fun getRefreshToken(): String? {
        // TODO: Implement secure storage for iOS (Keychain)
        return tokens?.refreshToken
    }

    actual override suspend fun isTokenExpired(): Boolean {
        val currentTokens = tokens ?: return true
        val currentTime = NSDate().timeIntervalSince1970.toLong()
        return currentTime >= currentTokens.tokenExpires
    }

    actual override suspend fun isRefreshTokenExpired(): Boolean {
        val currentTokens = tokens ?: return true
        val currentTime = NSDate().timeIntervalSince1970.toLong()
        return currentTime >= currentTokens.refreshTokenExpires
    }

    actual override suspend fun getTokenExpires(): Long? {
        return tokens?.tokenExpires
    }

    actual override suspend fun getRefreshTokenExpires(): Long? {
        return tokens?.refreshTokenExpires
    }

    actual override suspend fun clearTokens() {
        tokens = null
        // TODO: Implement secure storage for iOS (Keychain)
    }

    actual override suspend fun getUserName(): String? {
        val currentTokens = tokens ?: return null

        // Najpierw sprawdź czy mamy imię i nazwisko zapisane wprost w obiekcie AuthTokens
        if (!currentTokens.firstName.isNullOrBlank() || !currentTokens.lastName.isNullOrBlank()) {
            val first = currentTokens.firstName ?: ""
            val last = currentTokens.lastName ?: ""
            return "$first $last".trim()
        }

        // Jeśli nie, spróbuj wyciągnąć z tokenu JWT
        val payload = decodeTokenPayload(currentTokens.token)
        val firstName = payload["firstName"] ?: ""
        val lastName = payload["lastName"] ?: ""

        return if (firstName.isNotBlank() || lastName.isNotBlank()) {
            "$firstName $lastName".trim()
        } else {
            null
        }
    }
}
