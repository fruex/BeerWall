package com.fruex.beerwall.data.mapper

import com.fruex.beerwall.data.remote.dto.operators.PaymentOperatorResponse
import com.fruex.beerwall.data.remote.dto.operators.PaymentMethod as DtoPaymentMethod
import com.fruex.beerwall.domain.model.PaymentOperator
import com.fruex.beerwall.domain.model.PaymentMethod

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
