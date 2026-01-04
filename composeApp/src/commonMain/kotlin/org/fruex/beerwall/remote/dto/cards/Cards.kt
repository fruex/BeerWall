package org.fruex.beerwall.remote.dto.cards

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiError
import org.fruex.beerwall.remote.common.ApiResponse

@Serializable
data class CardItemDto(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val isPhysical: Boolean
)

@Serializable
data class CardActivationRequest(
    val cardId: String,
    val activate: Boolean
)

@Serializable
data class CardActivationResponse(
    override val data: CardActivationData? = null,
    override val error: ApiError? = null
) : ApiResponse<CardActivationData>

@Serializable
data class CardActivationData(
    val cardId: String,
    val isActive: Boolean,
    val status: String
)

@Serializable
data class GetCardsResponse(
    override val data: List<CardItemDto>? = null,
    override val error: ApiError? = null
) : ApiResponse<List<CardItemDto>>
