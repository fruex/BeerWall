package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.data.remote.dto.balance.BalanceResponse

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
