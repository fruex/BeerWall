package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.auth.AuthTokens
import com.fruex.beerwall.auth.GoogleAuthProvider
import com.fruex.beerwall.domain.repository.AuthRepository

/**
 * Przypadek użycia do logowania użytkownika przez Google.
 *
 * Obsługuje całą logikę: wywołanie Google Auth, weryfikację w backendzie, zapis tokenów.
 *
 * WAŻNE: Google ID Token ma krótką ważność (zazwyczaj 1 godzinę) i nie może być odświeżony.
 * Zawsze pobieramy świeży token od Google podczas logowania.
 *
 * @property authRepository Repozytorium autoryzacji.
 */
class GoogleSignInUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Wykonuje logowanie Google.
     *
     * @param googleAuthProvider Dostawca uwierzytelniania Google.
     * @return [Result] zawierający [AuthTokens] w przypadku sukcesu lub błąd.
     */
    suspend operator fun invoke(googleAuthProvider: GoogleAuthProvider): Result<AuthTokens> {
        return try {
            println("📱 Google Sign In: Requesting fresh token from Google")

            // Wywołaj Google Sign In dialog - ZAWSZE pobiera świeży token
            val localUser = googleAuthProvider.signIn()
                ?: return Result.failure(Exception("Anulowano logowanie Google"))

            println("✅ Google Sign In: Received token from Google")

            // Sprawdź czy token jest świeży
            if (localUser.isGoogleTokenExpired()) {
                println("⚠️ Google token już wygasł podczas pobierania")
                return Result.failure(Exception("Token Google wygasł. Spróbuj ponownie."))
            }

            println("📤 Sending Google token to .NET backend for verification")

            // Wyślij ID Token do backendu w celu weryfikacji i uzyskania tokenu .NET
            authRepository.googleSignIn(localUser.idToken)
        } catch (e: Exception) {
            println("❌ Google Sign In error: ${e.message}")
            Result.failure(e)
        }
    }
}
