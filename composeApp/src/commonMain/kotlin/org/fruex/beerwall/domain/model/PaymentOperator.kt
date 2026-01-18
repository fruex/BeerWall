package org.fruex.beerwall.domain.model

data class PaymentMethod(
    val paymentMethodId: Int,
    val name: String,
    val description: String,
    val image: String,
    val status: String
)

data class PaymentOperator(
    val type: String,
    val paymentMethods: List<PaymentMethod>
)
