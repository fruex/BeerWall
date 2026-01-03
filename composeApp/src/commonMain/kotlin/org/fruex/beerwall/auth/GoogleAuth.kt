package org.fruex.beerwall.auth

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
data class GoogleUser(
    val idToken: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)

interface GoogleAuthProvider {
    suspend fun signIn(): GoogleUser?
    suspend fun signOut()
    suspend fun getSignedInUser(): GoogleUser?
}

@Composable
expect fun rememberGoogleAuthProvider(): GoogleAuthProvider
