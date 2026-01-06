package org.fruex.beerwall.remote.dto.operators

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiError
import org.fruex.beerwall.remote.common.ApiResponse

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

@Serializable
data class GetPaymentOperatorsResponse(
    override val data: List<PaymentOperator>? = null,
    override val error: ApiError? = null
) : ApiResponse<List<PaymentOperator>>
