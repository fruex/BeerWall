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
    private val serverClientId = "220522932694-ghamgoqpqtmb0vk9ajnouiqe2h52ateb.apps.googleusercontent.com"

    override suspend fun signIn(): GoogleUser? = withContext(Dispatchers.Main) {
        Log.d("GoogleAuth", "Starting sign in process")
        
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            Log.d("GoogleAuth", "Calling getCredential")
            val result = credentialManager.getCredential(context, request)
            Log.d("GoogleAuth", "getCredential result received")
            val credential = result.credential

            val googleIdTokenCredential = when {
                credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    GoogleIdTokenCredential.createFrom(credential.data)
                }
                credential is GoogleIdTokenCredential -> {
                    credential
                }
                else -> null
            }

            if (googleIdTokenCredential != null) {
                googleIdTokenCredential.toGoogleUser()
            } else {
                Log.d("GoogleAuth", "Unknown credential type: ${credential.type}")
                null
            }
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Error getting credential", e)
            null
        }
    }

    private fun GoogleIdTokenCredential.toGoogleUser(): GoogleUser = GoogleUser(
        idToken = idToken,
        displayName = displayName,
        email = id,
        photoUrl = profilePictureUri?.toString()
    )

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
