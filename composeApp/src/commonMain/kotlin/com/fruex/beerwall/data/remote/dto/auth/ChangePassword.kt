package com.fruex.beerwall.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * DTO żądania zmiany hasła (dla zalogowanego użytkownika).
 *
 * @property newPassword Nowe hasło użytkownika.
 */
@Serializable
data class ChangePasswordRequest(
    val newPassword: String
)
