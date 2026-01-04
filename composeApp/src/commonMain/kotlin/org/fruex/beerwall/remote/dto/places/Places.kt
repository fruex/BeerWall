package org.fruex.beerwall.remote.dto.places

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiError
import org.fruex.beerwall.remote.common.ApiResponse

@Serializable
data class PlaceDto(
    val id: Int,
    val venueName: String,
    val fundsAvailable: Double
)

@Serializable
data class GetPlacesResponse(
    override val data: List<PlaceDto>? = null,
    override val error: ApiError? = null
) : ApiResponse<List<PlaceDto>>
