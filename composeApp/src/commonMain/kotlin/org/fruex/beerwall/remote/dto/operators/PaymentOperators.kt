package org.fruex.beerwall.remote.dto.operators

data class PaymentOperator(
    val id: String,
    val name: String
)

data class GetPaymentOperatorsResponse(
    val operators: List<PaymentOperator>
)
