package org.fruex.beerwall.presentation.mapper

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.ui.models.VenueBalance

fun Balance.toUi(): VenueBalance {
    return VenueBalance(
        venueName = venueName,
        balance = amount,
        loyaltyPoints = loyaltyPoints
    )
}

fun List<Balance>.toUi(): List<VenueBalance> {
    return map { it.toUi() }
}
