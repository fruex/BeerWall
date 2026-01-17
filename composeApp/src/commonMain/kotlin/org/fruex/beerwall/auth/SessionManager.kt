package org.fruex.beerwall.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.fruex.beerwall.domain.repository.AuthRepository

/**
 * Menedżer sesji (Singleton) odpowiedzialny za globalne zdarzenia
 * dotyczące sesji użytkownika, takie jak wygaśnięcie tokena.
 *
 * Zastępuje logikę wcześniej znajdującą się w AppViewModel/AppContainer.
 */
class SessionManager(
    private val authRepository: AuthRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isSessionExpired = MutableStateFlow(false)
    val isSessionExpired: StateFlow<Boolean> = _isSessionExpired.asStateFlow()

    /**
     * Wywoływane przez interseptor API lub repozytorium, gdy token wygasł.
     */
    fun onSessionExpired() {
        scope.launch {
            authRepository.logout()
            _isSessionExpired.value = true
        }
    }

    /**
     * Resetuje stan wygaśnięcia sesji (np. po ponownym zalogowaniu).
     */
    fun resetSession() {
        _isSessionExpired.value = false
    }
}
