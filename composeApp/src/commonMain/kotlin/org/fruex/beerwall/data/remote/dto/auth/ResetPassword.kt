package org.fruex.beerwall.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val resetCode: String,
    val newPassword: String
)
