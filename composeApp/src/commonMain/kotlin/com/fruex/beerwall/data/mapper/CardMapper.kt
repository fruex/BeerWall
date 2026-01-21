package com.fruex.beerwall.data.mapper

import com.fruex.beerwall.domain.model.Card
import com.fruex.beerwall.data.remote.dto.cards.CardResponse

/**
 * Mapuje [CardResponse] (DTO) na [Card] (Domain Model).
 */
fun CardResponse.toDomain(): Card {
    return Card(
        cardGuid = cardGuid,
        name = description,
        isActive = isActive,
        isPhysical = isPhysical
    )
}

/**
 * Mapuje listę [CardResponse] na listę [Card].
 */
fun List<CardResponse>.toDomain(): List<Card> {
    return map { it.toDomain() }
}
