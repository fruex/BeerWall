package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.data.remote.dto.operators.PaymentOperatorResponse
import org.fruex.beerwall.data.remote.dto.operators.PaymentMethod as DtoPaymentMethod
import org.fruex.beerwall.domain.model.PaymentOperator
import org.fruex.beerwall.domain.model.PaymentMethod

fun DtoPaymentMethod.toDomain(): PaymentMethod {
    return PaymentMethod(
        paymentMethodId = paymentMethodId,
        name = name,
        description = description,
        image = image,
        status = status
    )
}

fun PaymentOperatorResponse.toDomain(): PaymentOperator {
    return PaymentOperator(
        type = type,
        paymentMethods = paymentMethods.map { it.toDomain() }
    )
}

fun List<PaymentOperatorResponse>.toDomain(): List<PaymentOperator> {
    return map { it.toDomain() }
}
