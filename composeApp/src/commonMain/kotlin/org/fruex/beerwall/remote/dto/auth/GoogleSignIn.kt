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

@Serializable
data class RefreshTokenResponse(
    override val data: RefreshTokenResponseData? = null,
    override val error: ApiError? = null
) : ApiResponse<RefreshTokenResponseData>

@Serializable
data class EmailPasswordSignInRequest(
    val email: String,
    val password: String
)

@Serializable
data class TokenDto(
    val token: String,
    val tokenExpires: Long,
    val refreshToken: String,
    val refreshTokenExpires: Long
)

@Serializable
data class EmailPasswordSignInResponseData(
    val tokenDto: TokenDto,
    val is2FARequired: Boolean
)

@Serializable
data class EmailPasswordSignInResponse(
    override val data: EmailPasswordSignInResponseData? = null,
    override val error: ApiError? = null
) : ApiResponse<EmailPasswordSignInResponseData>
