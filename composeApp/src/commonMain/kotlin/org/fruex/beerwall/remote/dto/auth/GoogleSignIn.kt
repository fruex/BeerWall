package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

@Serializable
data class GoogleSignInResponse(
    val token: String,
    val tokenExpires: Long,
    val refreshToken: String,
    val refreshTokenExpires: Long
)

typealias GoogleSignInEnvelope = ApiEnvelope<GoogleSignInResponse>
