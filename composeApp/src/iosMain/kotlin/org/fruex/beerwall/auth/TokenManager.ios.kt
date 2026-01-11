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

    actual override suspend fun clearTokens() {
        tokens = null
        // TODO: Implement secure storage for iOS (Keychain)
    }
}
