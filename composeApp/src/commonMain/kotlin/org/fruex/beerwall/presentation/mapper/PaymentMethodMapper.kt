package org.fruex.beerwall.presentation.mapper

import org.fruex.beerwall.data.remote.dto.operators.PaymentMethod as PaymentMethodDto
import org.fruex.beerwall.ui.models.PaymentMethod as PaymentMethodUi

/**
 * Maps DTO to UI model for PaymentMethod.
 */
fun PaymentMethodDto.toUi(): PaymentMethodUi {
    return PaymentMethodUi(
        id = this.paymentMethodId,
        name = this.name,
        description = this.description,
        imageUrl = this.image,
        status = this.status
    )
}
