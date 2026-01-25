package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.model.SessionStatus
import com.fruex.beerwall.domain.repository.AuthRepository

/**
 * Przypadek użycia do sprawdzania sesji użytkownika przy starcie aplikacji.
 *
 * Odpowiedzialny za:
 * - Sprawdzenie czy użytkownik ma zapisany token.
 * - Automatyczne odświeżenie tokenu jeśli wygasł (ale refresh token jest ważny).
 * - Zwrócenie szczegółowego statusu sesji (Zalogowany, Wygasła, Gość, Pierwsze Uruchomienie).
 *
 * @property authRepository Repozytorium autoryzacji.
 */
class CheckSessionUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Sprawdza stan sesji użytkownika.
     *
     * @return [Result] zawierający [SessionStatus].
     */
    suspend operator fun invoke(): Result<SessionStatus> {
        return try {
            val status = authRepository.checkSessionStatus()
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
