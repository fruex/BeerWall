package com.fruex.beerwall.presentation.mapper

import com.fruex.beerwall.domain.model.Balance
import com.fruex.beerwall.ui.models.VenueBalance

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
