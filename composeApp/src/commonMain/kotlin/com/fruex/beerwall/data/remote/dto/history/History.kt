package com.fruex.beerwall.data.remote.dto.history

import kotlinx.serialization.Serializable
import com.fruex.beerwall.data.remote.common.ApiEnvelope

/**
 * DTO historii transakcji.
 *
 * @property transactionId Identyfikator transakcji.
 * @property commodityName Nazwa towaru (np. piwa).
 * @property startDateTime Data i czas rozpoczęcia transakcji (ISO 8601).
 * @property grossPrice Cena brutto.
 * @property capacity Ilość/pojemność (np. w mililitrach).
 */
@Serializable
data class TransactionResponse(
    val transactionId: Int,
    val commodityName: String,
    val startDateTime: String,
    val grossPrice: Double,
    val capacity: Int
)

/**
 * Alias dla koperty odpowiedzi pobierania historii transakcji.
 */
typealias GetHistoryEnvelope = ApiEnvelope<List<TransactionResponse>>
