package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * DTO żądania resetowania hasła.
 *
 * @property email Adres email użytkownika.
 */
@Serializable
data class ForgotPasswordRequest(
    val email: String
)
