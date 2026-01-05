package org.fruex.beerwall.presentation.mapper

import org.fruex.beerwall.domain.model.Card
import org.fruex.beerwall.ui.models.UserCard

fun Card.toUi(): UserCard {
    return UserCard(
        id = id,
        name = name,
        isActive = isActive,
        isPhysical = isPhysical
    )
}

fun List<Card>.toUi(): List<UserCard> {
    return map { it.toUi() }
}
