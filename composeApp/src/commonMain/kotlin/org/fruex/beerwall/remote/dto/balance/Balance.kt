package org.fruex.beerwall.remote.dto.balance

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiError
import org.fruex.beerwall.remote.common.ApiResponse

@Serializable
data class BalanceItem(
    val venueId: Int,
    val venueName: String,
    val balance: Double,
    val loyaltyPoints: Int
)

@Serializable
data class GetBalanceResponse(
    override val data: List<BalanceItem>? = null,
    override val error: ApiError? = null
) : ApiResponse<List<BalanceItem>>

@Serializable
data class TopUpRequest(
    val venueId: Int,
    val paymentMethodId: Int,
    val amount: Double
)

@Serializable
data class TopUpResponseData(
    val paymentId: String,
    val status: String
)

@Serializable
data class TopUpResponse(
    override val data: TopUpResponseData? = null,
    override val error: ApiError? = null
) : ApiResponse<TopUpResponseData>