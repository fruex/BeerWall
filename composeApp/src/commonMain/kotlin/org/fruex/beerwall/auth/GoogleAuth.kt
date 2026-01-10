package org.fruex.beerwall.auth

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
data class GoogleUser(
    val idToken: String,
    val tokenExpires: Long? = null,
    val refreshToken: String? = null,
    val refreshTokenExpires: Long? = null,
    val displayName: String? = null,
    val email: String? = null
)

interface GoogleAuthProvider {
    suspend fun signIn(): GoogleUser?
    suspend fun signOut()
    suspend fun getSignedInUser(): GoogleUser?
}

@Composable
expect fun rememberGoogleAuthProvider(): GoogleAuthProvider
