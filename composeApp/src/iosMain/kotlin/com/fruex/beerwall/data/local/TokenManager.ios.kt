package com.fruex.beerwall.data.local

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import com.fruex.beerwall.ui.models.UserProfile
import com.fruex.beerwall.domain.model.AuthTokens

actual fun currentTimeSeconds(): Long = NSDate().timeIntervalSince1970.toLong()

actual class TokenManagerImpl : TokenManager {
    private var tokens: AuthTokens? = null
    
    actual override suspend fun saveTokens(tokens: AuthTokens) {
        this.tokens = tokens
        _isFirstLaunch = false
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
        val currentTokens = tokens ?: return false
        val currentTime = NSDate().timeIntervalSince1970.toLong()
        return currentTime >= currentTokens.tokenExpires
    }

    actual override suspend fun isRefreshTokenExpired(): Boolean {
        val currentTokens = tokens ?: return false
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

    actual override suspend fun getUserProfile(): UserProfile? {
        val currentTokens = tokens ?: return null
        
        // Najpierw sprawdź czy mamy imię i nazwisko zapisane wprost w obiekcie AuthTokens
        val displayName = if (!currentTokens.firstName.isNullOrBlank() || !currentTokens.lastName.isNullOrBlank()) {
            val first = currentTokens.firstName ?: ""
            val last = currentTokens.lastName ?: ""
            "$first $last".trim()
        } else {
            // Jeśli nie, spróbuj wyciągnąć z tokenu JWT
            val payload = decodeTokenPayload(currentTokens.token)
            val firstName = payload["firstName"] ?: ""
            val lastName = payload["lastName"] ?: ""
            
            if (firstName.isNotBlank() || lastName.isNotBlank()) {
                "$firstName $lastName".trim()
            } else {
                null
            }
        }

        return if (displayName != null) {
            UserProfile(name = displayName)
        } else {
            null
        }
    }

    private var _isFirstLaunch = true

    actual override suspend fun isFirstLaunch(): Boolean {
        // TODO: Implement persistent storage for iOS
        return _isFirstLaunch
    }

    actual override suspend fun markFirstLaunchSeen() {
        // TODO: Implement persistent storage for iOS
        _isFirstLaunch = false
    }
}
