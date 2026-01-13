package org.fruex.beerwall.remote.dto.operators

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

@Serializable
data class PaymentMethod(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    val status: String
)

@Serializable
data class PaymentOperator(
    val type: String,
    val paymentMethods: List<PaymentMethod>
)

typealias GetPaymentOperatorsResponse = ApiEnvelope<List<PaymentOperator>>
