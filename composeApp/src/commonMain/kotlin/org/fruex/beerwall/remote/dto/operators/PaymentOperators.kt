package org.fruex.beerwall.remote.dto.operators

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

@Serializable
data class PaymentMethod(
    val paymentMethodId: Int,
    val name: String,
    val description: String,
    val image: String,
    val status: String
)

@Serializable
data class PaymentOperatorResponse(
    val type: String,
    val paymentMethods: List<PaymentMethod>
)

typealias GetPaymentOperatorsEnvelope = ApiEnvelope<List<PaymentOperatorResponse>>
