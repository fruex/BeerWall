package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiError
import org.fruex.beerwall.remote.common.ApiResponse

@Serializable
data class GoogleSignInRequest(
    val idToken: String
)

@Serializable
data class GoogleSignInResponseData(
    val token: String,
    val tokenExpires: Long,
    val refreshToken: String,
    val refreshTokenExpires: Long
)

@Serializable
data class GoogleSignInResponse(
    override val data: GoogleSignInResponseData? = null,
    override val error: ApiError? = null
) : ApiResponse<GoogleSignInResponseData>
