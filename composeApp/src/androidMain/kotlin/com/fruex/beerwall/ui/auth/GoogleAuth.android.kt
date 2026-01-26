package com.fruex.beerwall.ui.auth

import android.content.Context
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.R
import com.fruex.beerwall.getPlatform
import com.fruex.beerwall.log
import com.fruex.beerwall.domain.auth.GoogleAuthProvider
import com.fruex.beerwall.domain.model.GoogleUser
import java.io.InputStream
import java.io.OutputStream

@kotlinx.serialization.Serializable
private data class GoogleUserSession(val user: GoogleUser? = null)

private object GoogleUserSerializer : Serializer<GoogleUserSession> {
    override val defaultValue: GoogleUserSession = GoogleUserSession()
    private val platform = getPlatform()

    override suspend fun readFrom(input: InputStream): GoogleUserSession = try {
        val text = input.readBytes().decodeToString()
        if (text.isEmpty()) defaultValue else Json.decodeFromString<GoogleUserSession>(text)
    } catch (e: Exception) {
        platform.log("Error reading GoogleUserSession: ${e.message}", this, LogSeverity.ERROR)
        defaultValue
    }

    override suspend fun writeTo(t: GoogleUserSession, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(Json.encodeToString(GoogleUserSession.serializer(), t).encodeToByteArray())
        }
    }
}

private val Context.googleUserDataStore: DataStore<GoogleUserSession> by dataStore(
    fileName = "google_user.json",
    serializer = GoogleUserSerializer
)

class AndroidGoogleAuthProvider(private val context: Context) : GoogleAuthProvider {
    private val credentialManager = CredentialManager.create(context)
    private val serverClientId = context.getString(R.string.google_server_client_id)
    private val platform = getPlatform()

    override suspend fun signIn(): GoogleUser? = withContext(Dispatchers.Main) {
        runCatching {
            platform.log("Starting sign in process", this@AndroidGoogleAuthProvider, LogSeverity.DEBUG)

            // Zawsze pobieraj nowy token - nie używaj zapisanego
            val credential = credentialManager.getCredential(context, buildCredentialRequest()).credential
            platform.log("Credential received: ${credential.type}", this@AndroidGoogleAuthProvider, LogSeverity.DEBUG)

            credential.toGoogleIdTokenCredential()?.let { googleCredential ->
                val user = googleCredential.toGoogleUser()
                platform.log("Google user created: ${user.email}", this@AndroidGoogleAuthProvider, LogSeverity.INFO)
                platform.log("ID Token length: ${user.idToken.length}", this@AndroidGoogleAuthProvider, LogSeverity.DEBUG)
                saveUser(user)
                user
            } ?: run {
                platform.log("Unknown credential type: ${credential.type}", this@AndroidGoogleAuthProvider, LogSeverity.WARN)
                null
            }
        }.getOrElse { e ->
            platform.log("Error getting credential: ${e.message}", this@AndroidGoogleAuthProvider, LogSeverity.ERROR)
            null
        }
    }

    override suspend fun getSignedInUser(): GoogleUser? = withContext(Dispatchers.IO) {
        try {
            platform.log("Getting signed in user from DataStore", this@AndroidGoogleAuthProvider, LogSeverity.DEBUG)
            val session = context.googleUserDataStore.data.first()
            platform.log("Session retrieved: ${session.user?.email ?: "no user"}", this@AndroidGoogleAuthProvider, LogSeverity.DEBUG)
            session.user
        } catch (e: Exception) {
            platform.log("Error reading session: ${e.message}", this@AndroidGoogleAuthProvider, LogSeverity.ERROR)
            null
        }
    }

    override suspend fun signOut() {
        runCatching {
            clearUser()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }.onFailure { e ->
            platform.log("Error clearing credential state: ${e.message}", this@AndroidGoogleAuthProvider, LogSeverity.ERROR)
        }
    }

    private fun buildCredentialRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false) // Zawsze pokazuj wybór konta aby uzyskać świeży token
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

    private fun GoogleIdTokenCredential.toGoogleUser(): GoogleUser {
        val user = GoogleUser(
            idToken = idToken,
            displayName = displayName,
            email = id
        )

        // Loguj informacje o tokenie
        try {
            val parts = idToken.split(".")
            if (parts.size == 3) {
                val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP))
                val expMatch = """"exp"\s*:\s*(\d+)""".toRegex().find(payload)
                val expiration = expMatch?.groupValues?.get(1)?.toLongOrNull()

                if (expiration != null) {
                    val currentTime = System.currentTimeMillis() / 1000
                    val validForSeconds = expiration - currentTime
                    val validForMinutes = validForSeconds / 60
                    platform.log("Token valid for: $validForMinutes minutes ($validForSeconds seconds)", this@AndroidGoogleAuthProvider, LogSeverity.DEBUG)
                    platform.log("Token expires at: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(expiration * 1000))}", this@AndroidGoogleAuthProvider, LogSeverity.DEBUG)
                }
            }
        } catch (e: Exception) {
            platform.log("Error parsing token expiration: ${e.message}", this@AndroidGoogleAuthProvider, LogSeverity.WARN)
        }

        return user
    }

    private suspend fun saveUser(user: GoogleUser) {
        context.googleUserDataStore.updateData { it.copy(user = user) }
    }

    private suspend fun clearUser() {
        context.googleUserDataStore.updateData { it.copy(user = null) }
    }
}

@Composable
actual fun rememberGoogleAuthProvider(): GoogleAuthProvider {
    val context = LocalContext.current
    return remember(context) { AndroidGoogleAuthProvider(context) }
}
