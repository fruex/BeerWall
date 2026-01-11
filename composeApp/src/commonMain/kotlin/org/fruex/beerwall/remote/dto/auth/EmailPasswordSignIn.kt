package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiError
import org.fruex.beerwall.remote.common.ApiResponse

@Serializable
data class EmailPasswordSignInRequest(
    val email: String,
    val password: String
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
