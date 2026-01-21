package com.fruex.beerwall.presentation.mapper

import com.fruex.beerwall.domain.model.Card
import com.fruex.beerwall.ui.models.UserCard

fun Card.toUi(): UserCard {
    return UserCard(
        cardGuid = cardGuid,
        name = name,
        isActive = isActive,
        isPhysical = isPhysical
    )
}

fun List<Card>.toUi(): List<UserCard> {
    return map { it.toUi() }
}
