package com.fruex.beerwall.presentation.mapper

import com.fruex.beerwall.domain.model.Balance
import com.fruex.beerwall.ui.models.PremisesBalance

fun Balance.toUi(): PremisesBalance {
    return PremisesBalance(
        premisesId = premisesId,
        premisesName = premisesName,
        balance = balance,
        loyaltyPoints = loyaltyPoints,
        formattedBalance = "$balance zł",
        formattedLoyaltyPoints = "$loyaltyPoints pkt"
    )
}

fun List<Balance>.toUi(): List<PremisesBalance> {
    return map { it.toUi() }
}
