package org.fruex.beerwall.domain.model

data class Transaction(
    val id: String,
    val beerName: String,
    val date: String,
    val time: String,
    val amount: Double,
    val cardNumber: String
)
