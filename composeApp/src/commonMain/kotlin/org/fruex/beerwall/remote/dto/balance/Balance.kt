package org.fruex.beerwall.remote.dto.balance

import kotlinx.serialization.Serializable

/**
 * GET balance
 * Example response:
 * {
 *   "data": [
 *     {
 *       "venueName": "Beer Heaven",
 *       "amount": 150.50
 *     },
 *     {
 *       "venueName": "Pub Krakowski",
 *       "amount": 75.25
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
    val venueName: String,
    val amount: Double
)

data class GetBalanceResponse(
    val data: List<BalanceItem>
)
