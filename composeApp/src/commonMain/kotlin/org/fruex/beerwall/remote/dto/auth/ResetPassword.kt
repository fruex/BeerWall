package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * DTO żądania zmiany hasła przy użyciu kodu resetującego.
 *
 * @property email Adres email użytkownika.
 * @property resetCode Kod resetujący otrzymany w wiadomości email.
 * @property newPassword Nowe hasło użytkownika.
 */
@Serializable
data class ResetPasswordRequest(
    val email: String,
    val resetCode: String,
    val newPassword: String
)
