package org.fruex.beerwall.domain.model

data class Balance(
    val venueName: String,
    val amount: Double,
    val loyaltyPoints: Int
)
