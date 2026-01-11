package org.fruex.beerwall.domain.model

/**
 * Model reprezentujący kartę RFID użytkownika.
 *
 * @property id Unikalny identyfikator karty.
 * @property name Nazwa własna karty nadana przez użytkownika.
 * @property isActive Status aktywności karty (true - aktywna, false - zablokowana).
 * @property isPhysical Flaga określająca typ karty (fizyczna vs wirtualna).
 */
data class Card(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val isPhysical: Boolean
)
