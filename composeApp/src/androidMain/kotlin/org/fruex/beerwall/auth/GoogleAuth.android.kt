package org.fruex.beerwall.auth

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidGoogleAuthProvider(private val context: Context) : GoogleAuthProvider {
    private val credentialManager = CredentialManager.create(context)
    private val serverClientId = "220522932694-qgcu3mhkgna9jsp37q1g16s813ki5o7l.apps.googleusercontent.com"

    override suspend fun signIn(): GoogleUser? = withContext(Dispatchers.Main) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                GoogleUser(
                    idToken = googleIdTokenCredential.idToken,
                    displayName = googleIdTokenCredential.displayName,
                    email = googleIdTokenCredential.id,
                    photoUrl = googleIdTokenCredential.profilePictureUri?.toString()
                )
            } else if (credential is GoogleIdTokenCredential) {
                GoogleUser(
                    idToken = credential.idToken,
                    displayName = credential.displayName,
                    email = credential.id,
                    photoUrl = credential.profilePictureUri?.toString()
                )
            } else {
                Log.d("GoogleAuth", "Unknown credential type: ${credential.type}")
                null
            }
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Error getting credential", e)
            null
        }
    }

    override suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Error clearing credential state", e)
        }
    }
}

@Composable
actual fun rememberGoogleAuthProvider(): GoogleAuthProvider {
    val context = LocalContext.current
    return remember(context) { AndroidGoogleAuthProvider(context) }
}
