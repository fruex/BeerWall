package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.domain.model.Card
import org.fruex.beerwall.remote.dto.cards.CardItemDto

fun CardItemDto.toDomain(): Card {
    return Card(
        id = id,
        name = name,
        isActive = isActive,
        isPhysical = isPhysical
    )
}

fun List<CardItemDto>.toDomain(): List<Card> {
    return map { it.toDomain() }
}
