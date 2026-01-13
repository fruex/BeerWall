package org.fruex.beerwall.remote.dto.balance

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiError
import org.fruex.beerwall.remote.common.ApiResponse

@Serializable
data class BalanceItem(
    val premisesId: Int,
    val premisesName: String,
    val balance: Double,
    val loyalityPoints: Int
)

@Serializable
data class GetBalanceResponse(
    override val data: List<BalanceItem>? = null,
    override val error: ApiError? = null
) : ApiResponse<List<BalanceItem>>

@Serializable
data class TopUpRequest(
    val premisesId: Int,
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