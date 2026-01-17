package org.fruex.beerwall.data.remote.common

import kotlinx.serialization.Serializable

/**
 * Generyczna koperta odpowiedzi API.
 *
 * @param T Typ danych zwracanych w odpowiedzi.
 * @property data Dane odpowiedzi (jeśli sukces).
 * @property error Błąd (jeśli wystąpił).
 */
@Serializable
data class ApiEnvelope<T>(
    override val data: T? = null,
    override val error: ApiError? = null
) : ApiResponse<T>

/**
 * Interfejs dla odpowiedzi API.
 *
 * @param T Typ danych.
 */
interface ApiResponse<T> {
    val data: T?
    val error: ApiError?
}

/**
 * Model błędu zwracanego przez API.
 *
 * @property code Kod błędu.
 * @property message Wiadomość błędu.
 * @property details Szczegóły błędu (opcjonalnie).
 */
@Serializable
data class ApiError(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null
)
