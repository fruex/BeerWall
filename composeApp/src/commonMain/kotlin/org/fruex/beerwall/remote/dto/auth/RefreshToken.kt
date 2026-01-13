package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class RefreshTokenResponseData(
    val token: String,
    val tokenExpires: Long,
    val refreshToken: String,
    val refreshTokenExpires: Long
)

typealias RefreshTokenResponse = ApiEnvelope<RefreshTokenResponseData>
