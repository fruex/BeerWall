package org.fruex.beerwall.remote.dto.balance

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

@Serializable
data class GetBalanceResponseData(
    val premisesId: Int,
    val premisesName: String,
    val balance: Double,
    val loyalityPoints: Int
)

typealias GetBalanceResponse = ApiEnvelope<List<GetBalanceResponseData>>

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

typealias TopUpResponse = ApiEnvelope<TopUpResponseData>
