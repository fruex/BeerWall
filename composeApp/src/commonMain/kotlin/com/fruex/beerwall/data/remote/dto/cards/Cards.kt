package com.fruex.beerwall.data.remote.dto.cards

import com.fruex.beerwall.data.remote.common.ApiEnvelope
import kotlinx.serialization.Serializable

/**
 * DTO karty użytkownika.
 *
 * @property cardGuid Unikalny identyfikator (GUID) karty.
 * @property description Opis karty (np. nazwa nadana przez użytkownika).
 * @property isActive Czy karta jest aktywna.
 * @property isPhysical Czy jest to fizyczna karta.
 */
@Serializable
data class CardResponse(
    val cardGuid: String,
    val description: String,
    val isActive: Boolean,
    val isPhysical: Boolean
)

/**
 * DTO żądania aktualizacji karty.
 *
 * @property guid Unikalny identyfikator (GUID) karty.
 * @property isActive True, aby aktywować; False, aby dezaktywować.
 * @property description Opis karty.
 */
@Serializable
data class UpdateCardRequest(
    val guid: String,
    val description: String,
    val isActive: Boolean
)

/**
 * DTO żądania przypisania karty do użytkownika.
 *
 * @property guid Unikalny identyfikator (GUID) karty.
 * @property description Opis karty (opcjonalny).
 */
@Serializable
data class AssignCardRequest(
    val guid: String,
    val description: String
)

/**
 * Alias dla koperty odpowiedzi pobierania listy kart.
 */
typealias GetCardsEnvelope = ApiEnvelope<List<CardResponse>>
