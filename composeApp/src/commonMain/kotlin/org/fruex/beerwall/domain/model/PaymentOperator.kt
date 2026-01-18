package org.fruex.beerwall.domain.model

/**
 * Model operatora płatności (warstwa domeny).
 *
 * @property type Typ operatora (np. "Tpay").
 * @property paymentMethods Lista dostępnych metod płatności dla danego operatora.
 */
data class PaymentOperator(
    val type: String,
    val paymentMethods: List<PaymentMethod>
)
