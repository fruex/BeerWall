package com.fruex.beerwall.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.getPlatform

/**
 * Menedżer sesji użytkownika.
 *
 * Odpowiada za:
 * - Przechowywanie globalnego stanu zalogowania
 * - Obsługę wygaśnięcia sesji (np. 401 z API)
 */
class SessionManager {
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    /**
     * Ustawia stan zalogowania.
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        _isUserLoggedIn.update { isLoggedIn }
    }

    /**
     * Callback wywoływany gdy API zwróci 401 Unauthorized.
     */
    suspend fun onSessionExpired() {
        getPlatform().log("Sesja wygasła (401)", "SessionManager", LogSeverity.WARN)
        setLoggedIn(false)
    }
}
