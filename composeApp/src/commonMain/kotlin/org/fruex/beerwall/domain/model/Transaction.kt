package org.fruex.beerwall.domain.model

/**
 * Model domeny reprezentujący transakcję wykonaną przez użytkownika.
 *
 * @property transactionId Unikalny identyfikator transakcji.
 * @property commodityName Nazwa towaru lub usługi.
 * @property startDateTime Data i czas rozpoczęcia transakcji jako ciąg znaków.
 * // TODO: Rozważyć użycie typu `Instant` lub `LocalDateTime` z biblioteki `kotlinx-datetime` zamiast `String`.
 * @property grossPrice Cena brutto transakcji.
 * @property capacity Ilość (pojemność) związana z transakcją (np. mililitry piwa).
 */
data class Transaction(
    val transactionId: Int,
    val commodityName: String,
    val startDateTime: String,
    val grossPrice: Double,
    val capacity: Int
)
