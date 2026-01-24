package com.fruex.beerwall.domain.model

/**
 * Status sesji użytkownika.
 */
enum class SessionStatus {
    /** Użytkownik jest zalogowany i posiada aktywną sesję (lub ważny refresh token). */
    Authenticated,

    /** Sesja wygasła (refresh token stracił ważność). */
    Expired,

    /** Brak sesji, ale aplikacja została uruchomiona po raz pierwszy. */
    FirstLaunch,

    /** Brak sesji, standardowe uruchomienie (nie pierwsze). */
    Guest
}
