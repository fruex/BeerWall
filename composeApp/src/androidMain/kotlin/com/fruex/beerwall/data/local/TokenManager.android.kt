package com.fruex.beerwall.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.getPlatform
import com.fruex.beerwall.log
import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.domain.model.UserProfile
import java.io.InputStream
import java.io.OutputStream

@kotlinx.serialization.Serializable
private data class TokenSession(
    val tokens: AuthTokens? = null,
    val isFirstLaunch: Boolean = true
)

private object TokenSerializer : Serializer<TokenSession> {
    override val defaultValue: TokenSession = TokenSession()
    private val platform = getPlatform()
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    override suspend fun readFrom(input: InputStream): TokenSession = try {
        val text = input.readBytes().decodeToString()
        if (text.isBlank()) {
            defaultValue
        } else {
            json.decodeFromString<TokenSession>(text)
        }
    } catch (e: SerializationException) {
        platform.log("Error deserializing TokenSession: ${e.message}", this, LogSeverity.ERROR)
        defaultValue
    } catch (e: Exception) {
        platform.log("Error reading TokenSession: ${e.message}", this, LogSeverity.ERROR)
        defaultValue
    }

    override suspend fun writeTo(t: TokenSession, output: OutputStream) {
        withContext(Dispatchers.IO) {
            try {
                val text = json.encodeToString(TokenSession.serializer(), t)
                output.write(text.encodeToByteArray())
            } catch (e: Exception) {
                platform.log("Error writing TokenSession: ${e.message}", this, LogSeverity.ERROR)
                throw e
            }
        }
    }
}

private val Context.tokenDataStore: DataStore<TokenSession> by dataStore(
    fileName = "auth_tokens.json",
    serializer = TokenSerializer
)

actual fun currentTimeSeconds(): Long = System.currentTimeMillis() / 1000

actual class TokenManagerImpl(private val context: Context) : TokenManager {
    private val platform = getPlatform()
    
    actual override suspend fun saveTokens(tokens: AuthTokens) {
        try {
            context.tokenDataStore.updateData { it.copy(tokens = tokens, isFirstLaunch = false) }
            platform.log("Tokens saved successfully", this, LogSeverity.INFO)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            platform.log("Error saving tokens: ${e.message}", this, LogSeverity.ERROR)
            throw e
        }
    }

    actual override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        try {
            val session = context.tokenDataStore.data.first()
            session.tokens?.token
        } catch (e: Exception) {
            platform.log("Error reading token: ${e.message}", this, LogSeverity.ERROR)
            null
        }
    }

    actual override suspend fun getRefreshToken(): String? = withContext(Dispatchers.IO) {
        try {
            val session = context.tokenDataStore.data.first()
            session.tokens?.refreshToken
        } catch (e: Exception) {
            platform.log("Error reading refresh token: ${e.message}", this, LogSeverity.ERROR)
            null
        }
    }

    actual override suspend fun isTokenExpired(): Boolean = withContext(Dispatchers.IO) {
        try {
            val session = context.tokenDataStore.data.first()
            val tokens = session.tokens ?: return@withContext false // Missing token is not "expired"
            val currentTime = System.currentTimeMillis() / 1000
            currentTime >= tokens.tokenExpires
        } catch (e: Exception) {
            platform.log("Error checking token expiration: ${e.message}", this, LogSeverity.ERROR)
            false
        }
    }

    actual override suspend fun isRefreshTokenExpired(): Boolean = withContext(Dispatchers.IO) {
        try {
            val session = context.tokenDataStore.data.first()
            val tokens = session.tokens ?: return@withContext false // Missing token is not "expired"
            val currentTime = System.currentTimeMillis() / 1000
            currentTime >= tokens.refreshTokenExpires
        } catch (e: Exception) {
            platform.log("Error checking refresh token expiration: ${e.message}", this, LogSeverity.ERROR)
            false
        }
    }

    actual override suspend fun getTokenExpires(): Long? = withContext(Dispatchers.IO) {
        try {
            val session = context.tokenDataStore.data.first()
            session.tokens?.tokenExpires
        } catch (e: Exception) {
            platform.log("Error reading token expires: ${e.message}", this, LogSeverity.ERROR)
            null
        }
    }

    actual override suspend fun getRefreshTokenExpires(): Long? = withContext(Dispatchers.IO) {
        try {
            val session = context.tokenDataStore.data.first()
            session.tokens?.refreshTokenExpires
        } catch (e: Exception) {
            platform.log("Error reading refresh token expires: ${e.message}", this, LogSeverity.ERROR)
            null
        }
    }

    actual override suspend fun clearTokens() {
        try {
            context.tokenDataStore.updateData { it.copy(tokens = null) }
            platform.log("Tokens cleared", this, LogSeverity.INFO)
        } catch (e: CancellationException) {
            // CancellationException musi być przepuszczone dalej
            throw e
        } catch (e: Exception) {
            platform.log("Error clearing tokens: ${e.message}", this, LogSeverity.ERROR)
        }
    }

    actual override suspend fun getUserProfile(): UserProfile? = withContext(Dispatchers.IO) {
        try {
            val session = context.tokenDataStore.data.first()
            val tokens = session.tokens ?: return@withContext null
            
            // Najpierw sprawdź czy mamy imię i nazwisko zapisane wprost w obiekcie AuthTokens
            val displayName = if (!tokens.firstName.isNullOrBlank() || !tokens.lastName.isNullOrBlank()) {
                val first = tokens.firstName ?: ""
                val last = tokens.lastName ?: ""
                "$first $last".trim()
            } else {
                // Jeśli nie, spróbuj wyciągnąć z tokenu JWT
                val payload = decodeTokenPayload(tokens.token)
                val firstName = payload["firstName"] ?: ""
                val lastName = payload["lastName"] ?: ""
                
                if (firstName.isNotBlank() || lastName.isNotBlank()) {
                    "$firstName $lastName".trim()
                } else {
                    null
                }
            }

            if (displayName != null) {
                UserProfile(name = displayName)
            } else {
                null
            }
        } catch (e: Exception) {
            platform.log("Error getting user profile: ${e.message}", this, LogSeverity.ERROR)
            null
        }
    }

    actual override suspend fun isFirstLaunch(): Boolean = withContext(Dispatchers.IO) {
        try {
            val session = context.tokenDataStore.data.first()
            session.isFirstLaunch
        } catch (e: Exception) {
            platform.log("Error checking first launch: ${e.message}", this, LogSeverity.ERROR)
            true // Default to true on error to be safe (or false? Safe is usually treating as new user)
        }
    }

    actual override suspend fun markFirstLaunchSeen() {
        try {
            context.tokenDataStore.updateData { it.copy(isFirstLaunch = false) }
        } catch (e: Exception) {
            platform.log("Error marking first launch seen: ${e.message}", this, LogSeverity.ERROR)
        }
    }
}
