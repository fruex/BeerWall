package org.fruex.beerwall.auth

actual class TokenManagerImpl actual constructor() : TokenManager {
    private var tokens: AuthTokens? = null
    
    override suspend fun saveTokens(tokens: AuthTokens) {
        this.tokens = tokens
        // TODO: Implement secure storage for iOS (Keychain)
    }

    override suspend fun getToken(): String? {
        // TODO: Implement secure storage for iOS (Keychain)
        return tokens?.token
    }

    override suspend fun getRefreshToken(): String? {
        // TODO: Implement secure storage for iOS (Keychain)
        return tokens?.refreshToken
    }

    override suspend fun isTokenExpired(): Boolean {
        val currentTokens = tokens ?: return true
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime >= currentTokens.tokenExpires
    }

    override suspend fun clearTokens() {
        tokens = null
        // TODO: Implement secure storage for iOS (Keychain)
    }
}
