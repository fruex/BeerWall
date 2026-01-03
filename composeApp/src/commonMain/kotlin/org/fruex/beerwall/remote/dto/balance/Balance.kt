package org.fruex.beerwall.remote.dto.balance

import kotlinx.serialization.Serializable

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
    val locationName: String,
    val balance: Double
)

data class GetBalanceResponse(
    val data: List<BalanceItem>
)
