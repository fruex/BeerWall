package com.fruex.beerwall.auth

import kotlinx.coroutines.flow.StateFlow

/**
 * Interfejs menedżera sesji użytkownika.
 *
 * Odpowiada za:
 * - Przechowywanie globalnego stanu zalogowania
 * - Obsługę wygaśnięcia sesji (np. 401 z API)
 */
interface ISessionManager {
    /**
     * Stan zalogowania użytkownika.
     */
    val isUserLoggedIn: StateFlow<Boolean>

    /**
     * Ustawia stan zalogowania.
     */
    fun setLoggedIn(isLoggedIn: Boolean)

    /**
     * Callback wywoływany gdy API zwróci 401 Unauthorized.
     */
    suspend fun onSessionExpired()
}
