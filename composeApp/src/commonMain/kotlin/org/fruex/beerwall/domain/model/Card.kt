package org.fruex.beerwall.domain.model

data class Card(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val isPhysical: Boolean
)
