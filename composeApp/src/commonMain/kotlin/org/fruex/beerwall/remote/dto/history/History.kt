package org.fruex.beerwall.remote.dto.history

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

@Serializable
data class TransactionResponse(
    val transactionId: Int,
    val commodityName: String,
    val startDateTime: String,
    val grossPrice: Double,
    val capacity: Int
)

typealias GetHistoryEnvelope = ApiEnvelope<List<TransactionResponse>>
