package org.fruex.beerwall.remote.dto.cards

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

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
data class CardActivationData(
    val cardId: String,
    val isActive: Boolean,
    val status: String
)

typealias CardActivationResponse = ApiEnvelope<CardActivationData>

typealias GetCardsResponse = ApiEnvelope<List<CardItemDto>>
