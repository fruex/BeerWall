package org.fruex.beerwall.domain.model

data class Balance(
    val venueId: Int,
    val venueName: String,
    val amount: Double,
    val loyaltyPoints: Int
)
