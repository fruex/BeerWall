package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val resetCode: String,
    val newPassword: String
)
