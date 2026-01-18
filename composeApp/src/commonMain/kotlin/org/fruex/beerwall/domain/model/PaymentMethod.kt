package org.fruex.beerwall.domain.model

/**
 * Model metody płatności (warstwa domeny).
 *
 * @property paymentMethodId Identyfikator metody płatności.
 * @property name Nazwa metody płatności.
 * @property description Opis metody płatności.
 * @property image URL obrazka (logo) metody płatności.
 * @property status Status metody płatności (np. "Active").
 */
data class PaymentMethod(
    val paymentMethodId: Int,
    val name: String,
    val description: String,
    val image: String,
    val status: String
)
