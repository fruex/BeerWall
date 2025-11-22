package com.igibeer.beerwall.data.remote.dto.balance

/**
 * GET balance
 * Example response:
 * {
 *   "data": {
 *     "venueName": "Beer Heaven",
 *     "amount": 150.50
 *   }
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
data class GetBalanceResponse(
    val venueName: String,
    val amount: Double
)

//data class UpdateBalanceRequest(
//    val delta: Double,
//    val reason: String? = null
//)
//
//data class UpdateBalanceResponse(
//    val amount: Double,
//    val barName: String
//)
