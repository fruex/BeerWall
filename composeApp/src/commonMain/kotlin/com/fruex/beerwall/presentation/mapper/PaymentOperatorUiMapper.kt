package com.fruex.beerwall.presentation.mapper

import com.fruex.beerwall.domain.model.PaymentOperator
import com.fruex.beerwall.domain.model.PaymentMethod
import com.fruex.beerwall.ui.models.PaymentOperator as UiPaymentOperator
import com.fruex.beerwall.ui.models.PaymentMethod as UiPaymentMethod

fun PaymentMethod.toUi(): UiPaymentMethod {
    return UiPaymentMethod(
        paymentMethodId = paymentMethodId,
        name = name,
        description = description,
        image = image,
        status = status
    )
}

fun PaymentOperator.toUi(): UiPaymentOperator {
    return UiPaymentOperator(
        type = type,
        paymentMethods = paymentMethods.map { it.toUi() }
    )
}

fun List<PaymentOperator>.toUi(): List<UiPaymentOperator> {
    return map { it.toUi() }
}

fun List<PaymentMethod>.toUiMethods(): List<UiPaymentMethod> {
    return map { it.toUi() }
}
