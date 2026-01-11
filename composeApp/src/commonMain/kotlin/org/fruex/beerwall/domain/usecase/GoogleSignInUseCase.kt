package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.auth.GoogleAuthProvider
import org.fruex.beerwall.auth.GoogleUser
import org.fruex.beerwall.domain.repository.AuthRepository

/**
 * Use case do logowania użytkownika przez Google
 * Obsługuje całą logikę: wywołanie Google Auth, weryfikację w backendzie, zapis tokenów
 *
 * WAŻNE: Google ID Token ma krótką ważność (zazwyczaj 1 godzinę) i nie może być odświeżony.
 * Zawsze pobieramy świeży token od Google podczas logowania.
 */
class GoogleSignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(googleAuthProvider: GoogleAuthProvider): Result<GoogleUser> {
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
            authRepository.googleSignIn(localUser.idToken).map { backendUser ->
                // Połącz dane z Google (displayName, email) z danymi z backendu (tokeny)
                backendUser.copy(
                    displayName = localUser.displayName ?: backendUser.displayName,
                    email = localUser.email ?: backendUser.email
                )
            }
        } catch (e: Exception) {
            println("❌ Google Sign In error: ${e.message}")
            Result.failure(e)
        }
    }
}
