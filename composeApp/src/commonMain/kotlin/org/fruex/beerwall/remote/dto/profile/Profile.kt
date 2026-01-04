package org.fruex.beerwall.remote.dto.profile

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiError
import org.fruex.beerwall.remote.common.ApiResponse

@Serializable
data class ProfileDto(
    val loyaltyPoints: Int
)

@Serializable
data class GetProfileResponse(
    override val data: ProfileDto? = null,
    override val error: ApiError? = null
) : ApiResponse<ProfileDto>
