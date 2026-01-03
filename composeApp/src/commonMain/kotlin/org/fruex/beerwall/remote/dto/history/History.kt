package org.fruex.beerwall.remote.dto.history

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: String,
    val beerName: String,
    val date: String,
    val time: String,
    val amount: Double,
    val cardNumber: String
)
