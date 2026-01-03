package org.fruex.beerwall.remote.dto.cards

import kotlinx.serialization.Serializable

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
