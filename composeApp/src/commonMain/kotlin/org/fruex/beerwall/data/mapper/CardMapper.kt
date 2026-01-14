package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.domain.model.Card
import org.fruex.beerwall.remote.dto.cards.CardResponse

fun CardResponse.toDomain(): Card {
    return Card(
        id = cardGuid,
        name = description,
        isActive = isActive,
        isPhysical = isPhysical
    )
}

fun List<CardResponse>.toDomain(): List<Card> {
    return map { it.toDomain() }
}
