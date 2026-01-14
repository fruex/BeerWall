package org.fruex.beerwall.domain.model

data class Transaction(
    val transactionId: Int,
    val commodityName: String,
    val startDateTime: String,
    val grossPrice: Double,
    val capacity: Int
)
