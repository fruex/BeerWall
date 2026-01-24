package com.fruex.beerwall.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Model domeny reprezentujący transakcję wykonaną przez użytkownika.
 *
 * @property transactionId Unikalny identyfikator transakcji.
 * @property commodityName Nazwa towaru lub usługi.
 * @property startDateTime Data i czas rozpoczęcia transakcji.
 * @property grossPrice Cena brutto transakcji.
 * @property capacity Ilość (pojemność) związana z transakcją (np. mililitry piwa).
 * @property premisesName Nazwa lokalu.
 */
data class Transaction(
    val transactionId: Int,
    val commodityName: String,
    val startDateTime: LocalDateTime,
    val grossPrice: Double,
    val capacity: Int,
    val premisesName: String
)
