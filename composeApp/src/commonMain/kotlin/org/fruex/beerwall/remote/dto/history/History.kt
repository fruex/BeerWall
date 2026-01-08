package org.fruex.beerwall.remote.dto.history

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiError
import org.fruex.beerwall.remote.common.ApiResponse

@Serializable
data class TransactionDto(
    val id: String,
    val beverageName: String,
    val timestamp: String,
    val venueName: String,
    val amount: Double,
    val volumeMilliliters: Int
)

@Serializable
data class GetHistoryResponse(
    override val data: List<TransactionDto>? = null,
    override val error: ApiError? = null
) : ApiResponse<List<TransactionDto>>
