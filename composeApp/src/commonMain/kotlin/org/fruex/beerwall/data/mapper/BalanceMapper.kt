package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.model.PaymentMethod
import org.fruex.beerwall.domain.model.PaymentOperator
import org.fruex.beerwall.remote.dto.balance.BalanceResponse
import org.fruex.beerwall.remote.dto.operators.PaymentOperatorResponse
import org.fruex.beerwall.remote.dto.operators.PaymentMethod as PaymentMethodDto

/**
 * Mapuje [BalanceResponse] (DTO) na [Balance] (Domain Model).
 */
fun BalanceResponse.toDomain(): Balance {
    return Balance(
        premisesId = premisesId,
        premisesName = premisesName,
        balance = balance,
        loyaltyPoints = loyaltyPoints
    )
}

/**
 * Mapuje listę [BalanceResponse] na listę [Balance].
 */
fun List<BalanceResponse>.toDomain(): List<Balance> {
    return map { it.toDomain() }
}

/**
 * Mapuje [PaymentMethodDto] (DTO) na [PaymentMethod] (Domain Model).
 */
fun PaymentMethodDto.toDomain(): PaymentMethod {
    return PaymentMethod(
        paymentMethodId = paymentMethodId,
        name = name,
        description = description,
        image = image,
        status = status
    )
}

/**
 * Mapuje listę [PaymentMethodDto] (DTO) na listę [PaymentMethod] (Domain Model).
 */
fun List<PaymentMethodDto>.toDomainPaymentMethods(): List<PaymentMethod> {
    return map { it.toDomain() }
}

/**
 * Mapuje [PaymentOperatorResponse] (DTO) na [PaymentOperator] (Domain Model).
 */
fun PaymentOperatorResponse.toDomain(): PaymentOperator {
    return PaymentOperator(
        type = type,
        paymentMethods = paymentMethods.toDomainPaymentMethods()
    )
}

/**
 * Mapuje listę [PaymentOperatorResponse] na listę [PaymentOperator].
 */
fun List<PaymentOperatorResponse>.toDomainOperators(): List<PaymentOperator> {
    return map { it.toDomain() }
}
