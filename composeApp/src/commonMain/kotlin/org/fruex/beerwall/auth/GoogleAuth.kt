package org.fruex.beerwall.auth

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock

/**
 * Model użytkownika Google (zwracany przez Google Sign-In).
 *
 * @property idToken Token ID (JWT).
 * @property tokenExpires Czas wygaśnięcia tokenu.
 * @property refreshToken Token odświeżania (rzadko używany w tym kontekście na mobile).
 * @property refreshTokenExpires Czas wygaśnięcia tokenu odświeżania.
 * @property displayName Wyświetlana nazwa użytkownika.
 * @property email Adres email użytkownika.
 */
@Serializable
data class GoogleUser(
    val idToken: String,
    val tokenExpires: Long? = null,
    val refreshToken: String? = null,
    val refreshTokenExpires: Long? = null,
    val displayName: String? = null,
    val email: String? = null
) {
    /**
     * Sprawdza czy token Google wygasł.
     * Google ID Token to JWT z polem 'exp' (expiration time w sekundach od epoch).
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

            // JWT używa Base64 URL-safe encoding - musimy dodać padding i zamienić znaki
            var payload = parts[1]
                .replace('-', '+')
                .replace('_', '/')

            // Dodaj padding jeśli potrzebny
            when (payload.length % 4) {
                2 -> payload += "=="
                3 -> payload += "="
            }

            println("📦 Decoding payload (length: ${payload.length})")

            // Dekoduj payload
            val decodedPayload = Base64.Mime.decode(payload).decodeToString()
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
 */
interface GoogleAuthProvider {
    suspend fun signIn(): GoogleUser?
    suspend fun signOut()
    suspend fun getSignedInUser(): GoogleUser?
}

@Composable
expect fun rememberGoogleAuthProvider(): GoogleAuthProvider
