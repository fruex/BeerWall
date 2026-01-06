package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.remote.dto.balance.BalanceItem

fun BalanceItem.toDomain(): Balance {
    return Balance(
        venueId = venueId,
        venueName = venueName,
        amount = balance,
        loyaltyPoints = loyaltyPoints
    )
}

fun List<BalanceItem>.toDomain(): List<Balance> {
    return map { it.toDomain() }
}
