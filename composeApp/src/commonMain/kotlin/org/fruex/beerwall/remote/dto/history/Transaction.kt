package org.fruex.beerwall.remote.dto.history

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: Long,
    val date: String,
    val description: String,
    val amount: Double,
    val operator: String
)
