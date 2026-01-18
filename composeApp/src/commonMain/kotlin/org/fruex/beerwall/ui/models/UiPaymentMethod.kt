package org.fruex.beerwall.ui.models

/**
 * Model UI dla metody płatności.
 *
 * @property paymentMethodId Identyfikator metody płatności.
 * @property name Nazwa metody płatności.
 * @property description Opis metody płatności.
 * @property image URL obrazka (logo) metody płatności.
 * @property status Status metody płatności.
 */
data class UiPaymentMethod(
    val paymentMethodId: Int,
    val name: String,
    val description: String,
    val image: String,
    val status: String
)
