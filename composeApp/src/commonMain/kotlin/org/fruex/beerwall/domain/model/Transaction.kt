package org.fruex.beerwall.domain.model

/**
 * Model reprezentujący pojedynczą transakcję (zakup).
 *
 * @property id Unikalny identyfikator transakcji.
 * @property beverageName Nazwa zakupionego napoju/produktu.
 * @property timestamp Czas wykonania transakcji.
 * // TODO: Rozważyć użycie Instant/LocalDateTime z kotlinx-datetime zamiast String dla łatwiejszego formatowania i operacji na czasie.
 * @property venueName Nazwa lokalu, w którym dokonano zakupu.
 * @property amount Kwota transakcji.
 * @property volumeMilliliters Objętość w mililitrach (jeśli dotyczy).
 */
data class Transaction(
    val id: String,
    val beverageName: String,
    val timestamp: String,
    val venueName: String,
    val amount: Double,
    val volumeMilliliters: Int
)
