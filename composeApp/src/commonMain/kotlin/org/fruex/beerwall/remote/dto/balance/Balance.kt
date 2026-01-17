package org.fruex.beerwall.remote.dto.balance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

@Serializable
data class BalanceResponse(
    val premisesId: Int,
    val premisesName: String,
    val balance: Double,
    @SerialName("loyalityPoints") // API has typo, mapping to correct property name
    val loyaltyPoints: Int
)

typealias GetBalanceEnvelope = ApiEnvelope<List<BalanceResponse>>

@Serializable
data class TopUpRequest(
    val premisesId: Int,
    val paymentMethodId: Int,
    val amount: Double
)

