package org.fruex.beerwall.auth

import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class GoogleUser(
    val idToken: String,
    val tokenExpires: Long? = null,
    val refreshToken: String? = null,
    val refreshTokenExpires: Long? = null,
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null
) {
    /**
     * Sprawdza czy token Google wygasł.
     * Google ID Token to JWT z polem 'exp' (expiration time w sekundach od epoch).
     *
     * @return true jeśli token wygasł lub nie można go zweryfikować, false w przeciwnym razie.
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun isGoogleTokenExpired(): Boolean {
        return try {
            println("🔍 Checking Google token expiration...")

            // JWT ma format: header.payload.signature
            val parts = idToken.split(".")
            if (parts.size != 3) {
                println("❌ Invalid JWT format: expected 3 parts, got ${parts.size}")
                return true
            }

            // JWT używa Base64 URL-safe encoding - musimy zamienić znaki
            val payloadPart = parts[1]
            val paddedPayload = when (payloadPart.length % 4) {
                2 -> payloadPart + "=="
                3 -> payloadPart + "="
                else -> payloadPart
            }
            val standardPayload = paddedPayload.replace('-', '+').replace('_', '/')

            println("📦 Decoding payload (length: ${standardPayload.length})")

            val decodedBytes = Base64.Mime.decode(standardPayload)
            val decodedPayload = decodedBytes.decodeToString()

            println("✅ Decoded payload: ${decodedPayload.take(200)}...")

            // Wyciągnij wartość 'exp' z JSON
            val expMatch = """"exp"\s*:\s*(\d+)""".toRegex().find(decodedPayload)
            val expiration = expMatch?.groupValues?.get(1)?.toLongOrNull()

            if (expiration == null) {
                println("❌ Could not find 'exp' field in token")
                return true
            }

            // Sprawdź czy token wygasł (z małym buforem 30 sekund dla opóźnień sieciowych)
            // Używamy kotlinx-datetime Clock zamiast System.currentTimeMillis()
            val currentTime = Clock.System.now().epochSeconds
            val bufferSeconds = 30L // 30 sekund buffer na opóźnienia sieciowe
            val validForSeconds = expiration - currentTime
            val isExpired = currentTime >= (expiration - bufferSeconds)

            println("⏰ Current time: $currentTime")
            println("⏰ Token expires: $expiration")
            println("⏰ Valid for: ${validForSeconds / 60} minutes ($validForSeconds seconds)")
            println("⏰ Buffer: $bufferSeconds seconds")
            println("⏰ Is expired: $isExpired")

            isExpired
        } catch (e: Exception) {
            println("❌ Error checking Google token expiration: ${e.message}")
            e.printStackTrace()
            true // W razie błędu uznaj token za wygasły
        }
    }
}

/**
 * Interfejs dostawcy autoryzacji Google.
 * Implementowany w platform-specific code (Android/iOS).
 */
interface GoogleAuthProvider {
    /**
     * Rozpoczyna proces logowania.
     * @return Zalogowany użytkownik lub null w przypadku anulowania/błędu.
     */
    suspend fun signIn(): GoogleUser?

    /**
     * Wylogowuje użytkownika z Google.
     */
    suspend fun signOut()

    /**
     * Pobiera aktualnie zalogowanego użytkownika (jeśli istnieje cicha sesja).
     */
    suspend fun getSignedInUser(): GoogleUser?
}

@Composable
expect fun rememberGoogleAuthProvider(): GoogleAuthProvider
