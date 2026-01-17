package org.fruex.beerwall.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * DTO żądania rejestracji nowego użytkownika.
 *
 * @property email Adres email użytkownika.
 * @property password Hasło użytkownika.
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String
)
