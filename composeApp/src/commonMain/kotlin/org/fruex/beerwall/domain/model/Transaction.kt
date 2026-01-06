package org.fruex.beerwall.domain.model

data class Transaction(
    val id: String,
    val beverageName: String,
    val timestamp: String,
    val amount: Double,
    val volumeMilliliters: Int
)
