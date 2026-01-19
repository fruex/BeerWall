package com.fruex.beerwall.data.remote.dto.operators

import kotlinx.serialization.Serializable
import com.fruex.beerwall.data.remote.common.ApiEnvelope

/**
 * Model metody płatności.
 *
 * @property paymentMethodId Identyfikator metody płatności.
 * @property name Nazwa metody płatności.
 * @property description Opis metody płatności.
 * @property image URL obrazka (logo) metody płatności.
 * @property status Status metody płatności (np. "Active").
 */
@Serializable
data class PaymentMethod(
    val paymentMethodId: Int,
    val name: String,
    val description: String,
    val image: String,
    val status: String
)

/**
 * Model operatora płatności.
 *
 * @property type Typ operatora (np. "Tpay").
 * @property paymentMethods Lista dostępnych metod płatności dla danego operatora.
 */
@Serializable
data class PaymentOperatorResponse(
    val type: String,
    val paymentMethods: List<PaymentMethod>
)

/**
 * Alias dla koperty odpowiedzi pobierania operatorów płatności.
 */
typealias GetPaymentOperatorsEnvelope = ApiEnvelope<List<PaymentOperatorResponse>>
