package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

typealias RefreshTokenResponse = AuthResponse

typealias RefreshTokenEnvelope = ApiEnvelope<RefreshTokenResponse>
