package org.fruex.beerwall.remote.dto.balance

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

@Serializable
data class BalanceResponse(
    val premisesId: Int,
    val premisesName: String,
    val balance: Double,
    val loyalityPoints: Int
)

typealias GetBalanceEnvelope = ApiEnvelope<List<BalanceResponse>>

@Serializable
data class TopUpRequest(
    val premisesId: Int,
    val paymentMethodId: Int,
    val amount: Double
)

@Serializable
data class TopUpResponse(
    val paymentId: String,
    val status: String
)

typealias TopUpEnvelope = ApiEnvelope<TopUpResponse>
