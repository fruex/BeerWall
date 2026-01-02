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
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object GoogleUserSerializer : Serializer<GoogleUser?> {
    override val defaultValue: GoogleUser? = null

    override suspend fun readFrom(input: InputStream): GoogleUser? {
        return try {
            val text = input.readBytes().decodeToString()
            if (text.isEmpty()) null else Json.decodeFromString<GoogleUser>(text)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun writeTo(t: GoogleUser?, output: OutputStream) {
        t?.let {
            val json = Json.encodeToString(GoogleUser.serializer(), it)
            output.write(json.encodeToByteArray())
        }
    }
}

private val Context.googleUserDataStore: DataStore<GoogleUser?> by dataStore(
    fileName = "google_user.json",
    serializer = GoogleUserSerializer
)

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
                val googleUser = googleIdTokenCredential.toGoogleUser()
                saveUser(googleUser)
                googleUser
            } else {
                Log.d("GoogleAuth", "Unknown credential type: ${credential.type}")
                null
            }
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Error getting credential", e)
            null
        }
    }

    private suspend fun saveUser(user: GoogleUser) {
        context.googleUserDataStore.updateData { user }
    }

    private suspend fun clearUser() {
        context.googleUserDataStore.updateData { null }
    }

    override suspend fun getSignedInUser(): GoogleUser? = withContext(Dispatchers.IO) {
        context.googleUserDataStore.data.firstOrNull()
    }

    private fun GoogleIdTokenCredential.toGoogleUser(): GoogleUser = GoogleUser(
        idToken = idToken,
        displayName = displayName,
        email = id,
        photoUrl = profilePictureUri?.toString()
    )

    override suspend fun signOut() {
        try {
            clearUser()
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
