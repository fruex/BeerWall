package org.fruex.beerwall.domain.model

data class Balance(
    val premisesId: Int,
    val premisesName: String,
    val balance: Double,
    val loyalityPoints: Int
)
