package com.fruex.beerwall.domain.model

import kotlinx.serialization.Serializable

/**
 * Model tokenów autoryzacyjnych aplikacji.
 *
 * @property token Token dostępu (JWT).
 * @property tokenExpires Czas wygaśnięcia tokenu dostępu.
 * @property refreshToken Token odświeżania.
 * @property refreshTokenExpires Czas wygaśnięcia tokenu odświeżania.
 * @property firstName Imię użytkownika (wyciągnięte z tokenu).
 * @property lastName Nazwisko użytkownika (wyciągnięte z tokenu).
 */
@Serializable
data class AuthTokens(
    val token: String,
    val tokenExpires: Long,
    val refreshToken: String,
    val refreshTokenExpires: Long,
    val firstName: String? = null,
    val lastName: String? = null
)
