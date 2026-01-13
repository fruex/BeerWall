package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.remote.dto.balance.GetBalanceResponseData

fun GetBalanceResponseData.toDomain(): Balance {
    return Balance(
        premisesId = premisesId,
        premisesName = premisesName,
        balance = balance,
        loyalityPoints = loyalityPoints
    )
}

fun List<GetBalanceResponseData>.toDomain(): List<Balance> {
    return map { it.toDomain() }
}
