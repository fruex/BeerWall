package org.fruex.beerwall.remote.dto.history

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

@Serializable
data class TransactionResponse(
    val id: String,
    val beverageName: String,
    val timestamp: String,
    val venueName: String,
    val amount: Double,
    val volumeMilliliters: Int
)

typealias GetHistoryEnvelope = ApiEnvelope<List<TransactionResponse>>
