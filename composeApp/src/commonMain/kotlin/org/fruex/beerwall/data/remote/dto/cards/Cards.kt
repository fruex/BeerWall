package org.fruex.beerwall.data.remote.dto.cards

import kotlinx.serialization.Serializable
import org.fruex.beerwall.data.remote.common.ApiEnvelope

@Serializable
data class CardResponse(
    val cardGuid: String,
    val description: String,
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
    val cardId: String,
    val isActive: Boolean,
    val status: String
)

@Serializable
data class AssignCardRequest(
    val guid: String,
    val description: String
)

typealias CardActivationEnvelope = ApiEnvelope<CardActivationResponse>

typealias GetCardsEnvelope = ApiEnvelope<List<CardResponse>>
