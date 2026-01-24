package com.fruex.beerwall.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * DTO żądania zmiany hasła (dla zalogowanego użytkownika).
 *
 * @property oldPassword Stare hasło użytkownika.
 * @property newPassword Nowe hasło użytkownika.
 */
@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
