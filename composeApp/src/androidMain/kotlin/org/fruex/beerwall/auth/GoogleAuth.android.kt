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
import org.fruex.beerwall.R
import java.io.InputStream
import java.io.OutputStream

private object GoogleUserSerializer : Serializer<GoogleUser?> {
    override val defaultValue: GoogleUser? = null

    override suspend fun readFrom(input: InputStream): GoogleUser? = try {
        val text = input.readBytes().decodeToString()
        if (text.isEmpty()) null else Json.decodeFromString<GoogleUser>(text)
    } catch (e: Exception) {
        Log.e("GoogleUserSerializer", "Error reading GoogleUser", e)
        null
    }

    override suspend fun writeTo(t: GoogleUser?, output: OutputStream) {
        t?.let {
            output.write(Json.encodeToString(GoogleUser.serializer(), it).encodeToByteArray())
        }
    }
}

private val Context.googleUserDataStore: DataStore<GoogleUser?> by dataStore(
    fileName = "google_user.json",
    serializer = GoogleUserSerializer
)

class AndroidGoogleAuthProvider(private val context: Context) : GoogleAuthProvider {
    private val credentialManager = CredentialManager.create(context)
    private val serverClientId = context.getString(R.string.google_server_client_id)

    override suspend fun signIn(): GoogleUser? = withContext(Dispatchers.Main) {
        runCatching {
            Log.d(TAG, "Starting sign in process")
            val credential = credentialManager.getCredential(context, buildCredentialRequest()).credential
            Log.d(TAG, "Credential received")

            credential.toGoogleIdTokenCredential()?.let { googleCredential ->
                googleCredential.toGoogleUser().also { saveUser(it) }
            } ?: run {
                Log.d(TAG, "Unknown credential type: ${credential.type}")
                null
            }
        }.getOrElse { e ->
            Log.e(TAG, "Error getting credential", e)
            null
        }
    }

    override suspend fun getSignedInUser(): GoogleUser? = withContext(Dispatchers.IO) {
        context.googleUserDataStore.data.firstOrNull()
    }

    override suspend fun signOut() {
        runCatching {
            clearUser()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }.onFailure { e ->
            Log.e(TAG, "Error clearing credential state", e)
        }
    }

    private fun buildCredentialRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    private fun androidx.credentials.Credential.toGoogleIdTokenCredential(): GoogleIdTokenCredential? = when {
        this is CustomCredential && type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL ->
            GoogleIdTokenCredential.createFrom(data)
        this is GoogleIdTokenCredential -> this
        else -> null
    }

    private fun GoogleIdTokenCredential.toGoogleUser(): GoogleUser = GoogleUser(
        idToken = idToken,
        displayName = displayName,
        email = id,
        photoUrl = profilePictureUri?.toString()
    )

    private suspend fun saveUser(user: GoogleUser) {
        context.googleUserDataStore.updateData { user }
    }

    private suspend fun clearUser() {
        context.googleUserDataStore.updateData { null }
    }

    companion object {
        private const val TAG = "GoogleAuth"
    }
}

@Composable
actual fun rememberGoogleAuthProvider(): GoogleAuthProvider {
    val context = LocalContext.current
    return remember(context) { AndroidGoogleAuthProvider(context) }
}
