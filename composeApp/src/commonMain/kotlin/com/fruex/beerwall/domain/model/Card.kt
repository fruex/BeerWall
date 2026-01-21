package com.fruex.beerwall.domain.model

/**
 * Model domeny reprezentujący kartę przypisaną do użytkownika.
 *
 * @property cardGuid Unikalny identyfikator karty.
 * @property name Nazwa lub opis karty nadany przez użytkownika.
 * @property isActive Flaga określająca, czy karta jest aktywna.
 * @property isPhysical Flaga określająca, czy jest to fizyczna karta (czy np. wirtualna).
 */
data class Card(
    val cardGuid: String,
    val name: String,
    val isActive: Boolean,
    val isPhysical: Boolean
)
