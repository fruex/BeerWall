package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

typealias ChangePasswordResponse = Unit
typealias ChangePasswordEnvelope = ApiEnvelope<ChangePasswordResponse>
