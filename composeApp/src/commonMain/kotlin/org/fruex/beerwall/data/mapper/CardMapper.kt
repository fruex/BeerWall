package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.domain.model.Card
import org.fruex.beerwall.remote.dto.cards.CardResponse

/**
 * Mapuje [CardResponse] (DTO) na [Card] (Domain Model).
 */
fun CardResponse.toDomain(): Card {
    return Card(
        id = cardGuid,
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
