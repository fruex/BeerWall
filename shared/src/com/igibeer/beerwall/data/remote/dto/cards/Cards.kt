package com.igibeer.beerwall.data.remote.dto.cards

enum class CardType {
    DEFAULT,
    GOLD
}

data class CardSummary(
    val id: String, // GUID format
    val type: CardType,
    val isVirtual: Boolean,
    val isActive: Boolean
)

data class GetCardsResponse(
    val data: List<CardSummary>
)

data class CardActivationRequest(
    val cardId: String, // GUID format
    val active: Boolean
)

data class CardActivationResponse(
    val cardId: String, // GUID format
    val active: Boolean
)
