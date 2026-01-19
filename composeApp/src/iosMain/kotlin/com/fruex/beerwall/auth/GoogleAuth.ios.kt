package com.fruex.beerwall.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class IosGoogleAuthProvider : GoogleAuthProvider {
    override suspend fun signIn(): GoogleUser? {
        // TODO: Implement Google Sign-In for iOS
        return null
    }

    override suspend fun signOut() {
        // TODO: Implement Google Sign-Out for iOS
    }

    override suspend fun getSignedInUser(): GoogleUser? {
        // TODO: Implement Google session check for iOS
        return null
    }
}

@Composable
actual fun rememberGoogleAuthProvider(): GoogleAuthProvider {
    return remember { IosGoogleAuthProvider() }
}
