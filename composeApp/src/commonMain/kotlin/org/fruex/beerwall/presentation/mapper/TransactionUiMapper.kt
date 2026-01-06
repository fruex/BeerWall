package org.fruex.beerwall.presentation.mapper

import kotlinx.datetime.LocalDate
import org.fruex.beerwall.domain.model.Transaction
import org.fruex.beerwall.ui.models.DailyTransactions

private val monthNames = listOf(
    "sty", "lut", "mar", "kwi", "maj", "cze",
    "lip", "sie", "wrz", "paź", "lis", "gru"
)

fun Transaction.toUi(): org.fruex.beerwall.ui.models.Transaction {
    return org.fruex.beerwall.ui.models.Transaction(
        id = id,
        beverageName = beverageName,
        timestamp = timestamp,
        amount = amount,
        volumeMilliliters = volumeMilliliters
    )
}

fun List<Transaction>.toUi(): List<org.fruex.beerwall.ui.models.Transaction> {
    return map { it.toUi() }
}

fun List<Transaction>.groupByDate(): List<DailyTransactions> {
    return groupBy { it.timestamp.substringBefore("T") }
        .map { (dateString, transactions) ->
            val formattedDate = try {
                val date = LocalDate.parse(dateString)
                "${date.dayOfMonth} ${monthNames[date.monthNumber - 1]}"
            } catch (e: Exception) {
                dateString.uppercase()
            }

            DailyTransactions(
                date = formattedDate,
                transactions = transactions.toUi()
            )
        }
}
