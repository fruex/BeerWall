package org.fruex.beerwall.ui.models

import androidx.compose.runtime.Immutable

/**
 * Model metody płatności (warstwa UI).
 *
 * @property paymentMethodId Identyfikator metody płatności.
 * @property name Nazwa metody płatności.
 * @property description Opis metody płatności.
 * @property image URL obrazka (logo) metody płatności.
 * @property status Status metody płatności.
 */
@Immutable
data class PaymentMethod(
    val paymentMethodId: Int,
    val name: String,
    val description: String,
    val image: String,
    val status: String
)
