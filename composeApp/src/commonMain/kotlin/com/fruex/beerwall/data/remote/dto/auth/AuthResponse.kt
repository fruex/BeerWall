package com.fruex.beerwall.data.remote.dto.auth

import kotlinx.serialization.Serializable
import com.fruex.beerwall.data.remote.common.ApiEnvelope

/**
 * DTO odpowiedzi logowania.
 *
 * @property token Token dostępu.
 * @property tokenExpires Czas wygaśnięcia tokenu.
 * @property refreshToken Token odświeżania.
 * @property refreshTokenExpires Czas wygaśnięcia tokenu odświeżania.
 */
@Serializable
data class AuthResponse(
    val token: String,
    val tokenExpires: Long,
    val refreshToken: String,
    val refreshTokenExpires: Long
)

/**
 * Alias dla koperty odpowiedzi logowania.
 */
typealias AuthEnvelope = ApiEnvelope<AuthResponse>
