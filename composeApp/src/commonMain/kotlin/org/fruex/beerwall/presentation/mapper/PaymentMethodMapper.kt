package org.fruex.beerwall.presentation.mapper

import org.fruex.beerwall.data.remote.dto.operators.PaymentMethod
import org.fruex.beerwall.ui.models.UiPaymentMethod

/**
 * Mapuje obiekt DTO [PaymentMethod] na model UI [UiPaymentMethod].
 */
fun PaymentMethod.toUi(): UiPaymentMethod = UiPaymentMethod(
    paymentMethodId = paymentMethodId,
    name = name,
    description = description,
    image = image,
    status = status
)

/**
 * Mapuje listę obiektów DTO [PaymentMethod] na listę modeli UI [UiPaymentMethod].
 */
fun List<PaymentMethod>.toUi(): List<UiPaymentMethod> = map { it.toUi() }
