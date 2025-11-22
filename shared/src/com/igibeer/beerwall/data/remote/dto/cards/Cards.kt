package com.igibeer.beerwall.data.remote.dto.cards

data class CardSummary(
    val id: String,
    val last4: String,
    val brand: String,
    val active: Boolean
)

data class GetCardsResponse(
    val cards: List<CardSummary>
)

data class GetCardDetailsResponse(
    val id: String,
    val brand: String,
    val last4: String,
    val holderName: String?,
    val active: Boolean,
    val createdAt: String
)

data class CardActivationRequest(
    val cardId: String,
    val active: Boolean
)

data class CardActivationResponse(
    val cardId: String,
    val active: Boolean
)
