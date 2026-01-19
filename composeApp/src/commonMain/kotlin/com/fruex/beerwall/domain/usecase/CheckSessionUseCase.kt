package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.AuthRepository

/**
 * Przypadek użycia do sprawdzania sesji użytkownika przy starcie aplikacji.
 *
 * Odpowiedzialny za:
 * - Sprawdzenie czy użytkownik ma zapisany token.
 * - Automatyczne odświeżenie tokenu jeśli wygasł (ale refresh token jest ważny).
 * - Zwrócenie informacji czy użytkownik jest zalogowany.
 *
 * @property authRepository Repozytorium autoryzacji.
 */
class CheckSessionUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Sprawdza stan sesji użytkownika.
     *
     * @return [Result] zawierający `true` jeśli użytkownik jest zalogowany, `false` w przeciwnym razie.
     */
    suspend operator fun invoke(): Result<Boolean> {
        return try {
            // Sprawdź czy użytkownik ma zapisane tokeny
            val isLoggedIn = authRepository.isUserLoggedIn()
            
            if (isLoggedIn) {
                // Spróbuj odświeżyć token jeśli to potrzebne
                // authRepository.refreshToken() zostanie wywołane automatycznie przez interceptor
                // przy pierwszym żądaniu do API, jeśli token wygasł
                Result.success(true)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
