package org.fruex.beerwall.remote.dto.balance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiError
import org.fruex.beerwall.remote.common.ApiResponse

/**
 * GET balance
 * Example response:
 * {
 *   "data": [
 *     {
 *       "venueName": "Beer Heaven",
 *       "balance": 150.50
 *     },
 *     {
 *       "venueName": "Pub Krakowski",
 *       "balance": 75.25
 *     }
 *   ]
 * }
 * Error response:
 * {
 *   "error": {
 *     "code": "BALANCE_NOT_FOUND",
 *     "message": "Balance not found for this user",
 *     "details": {
 *       "userId": "123"
 *     }
 *   }
 * }
 */
@Serializable
data class BalanceItem(
    val venueId: Int,
    @SerialName("venueName")
    val venueName: String,
    val balance: Double,
    val loyaltyPoints: Int
)

@Serializable
data class TopUpRequest(
    val venueId: Int,
    val paymentMethodId: Int,
    val amount: Double
)

@Serializable
data class TopUpResponseData(
    val message: String,
    val newBalance: Double
)

@Serializable
data class TopUpResponse(
    override val data: TopUpResponseData? = null,
    override val error: ApiError? = null
) : ApiResponse<TopUpResponseData>

@Serializable
data class GetBalanceResponse(
    override val data: List<BalanceItem>? = null,
    override val error: ApiError? = null
) : ApiResponse<List<BalanceItem>>
