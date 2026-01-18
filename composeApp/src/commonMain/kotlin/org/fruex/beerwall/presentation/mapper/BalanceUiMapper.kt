package org.fruex.beerwall.presentation.mapper

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.model.PaymentMethod
import org.fruex.beerwall.ui.models.VenueBalance
import org.fruex.beerwall.ui.models.PaymentMethod as PaymentMethodUi

fun Balance.toUi(): VenueBalance {
    return VenueBalance(
        premisesId = premisesId,
        premisesName = premisesName,
        balance = balance,
        loyaltyPoints = loyaltyPoints
    )
}

fun List<Balance>.toUi(): List<VenueBalance> {
    return map { it.toUi() }
}

fun PaymentMethod.toUi(): PaymentMethodUi {
    return PaymentMethodUi(
        paymentMethodId = paymentMethodId,
        name = name,
        description = description,
        image = image,
        status = status
    )
}

fun List<PaymentMethod>.toUiPaymentMethods(): List<PaymentMethodUi> {
    return map { it.toUi() }
}
