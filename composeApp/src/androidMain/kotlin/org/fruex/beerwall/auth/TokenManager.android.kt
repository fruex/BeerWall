package org.fruex.beerwall.auth

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@kotlinx.serialization.Serializable
private data class TokenSession(val tokens: AuthTokens? = null)

private object TokenSerializer : Serializer<TokenSession> {
    override val defaultValue: TokenSession = TokenSession()

    override suspend fun readFrom(input: InputStream): TokenSession = try {
        val text = input.readBytes().decodeToString()
        if (text.isEmpty()) defaultValue else Json.decodeFromString<TokenSession>(text)
    } catch (e: Exception) {
        Log.e("TokenSerializer", "Error reading TokenSession", e)
        defaultValue
    }

    override suspend fun writeTo(t: TokenSession, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(Json.encodeToString(TokenSession.serializer(), t).encodeToByteArray())
        }
    }
}

private val Context.tokenDataStore: DataStore<TokenSession> by dataStore(
    fileName = "auth_tokens.json",
    serializer = TokenSerializer
)

actual class TokenManagerImpl(private val context: Context) : TokenManager {
    
    override suspend fun saveTokens(tokens: AuthTokens) {
        context.tokenDataStore.updateData { it.copy(tokens = tokens) }
        Log.d("TokenManager", "Tokens saved")
    }

    override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        try {
            val session = context.tokenDataStore.data.first()
            session.tokens?.token
        } catch (e: Exception) {
            Log.e("TokenManager", "Error reading token", e)
            null
        }
    }

    override suspend fun getRefreshToken(): String? = withContext(Dispatchers.IO) {
        try {
            val session = context.tokenDataStore.data.first()
            session.tokens?.refreshToken
        } catch (e: Exception) {
            Log.e("TokenManager", "Error reading refresh token", e)
            null
        }
    }

    override suspend fun isTokenExpired(): Boolean = withContext(Dispatchers.IO) {
        try {
            val session = context.tokenDataStore.data.first()
            val tokens = session.tokens ?: return@withContext true
            val currentTime = System.currentTimeMillis() / 1000
            currentTime >= tokens.tokenExpires
        } catch (e: Exception) {
            Log.e("TokenManager", "Error checking token expiration", e)
            true
        }
    }

    override suspend fun clearTokens() {
        context.tokenDataStore.updateData { it.copy(tokens = null) }
        Log.d("TokenManager", "Tokens cleared")
    }
}
