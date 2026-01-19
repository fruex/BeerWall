package com.fruex.beerwall.presentation.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import com.fruex.beerwall.domain.model.Transaction
import com.fruex.beerwall.ui.models.DailyTransactions

private val monthNames = listOf(
    "sty", "lut", "mar", "kwi", "maj", "cze",
    "lip", "sie", "wrz", "paź", "lis", "gru"
)

fun Transaction.toUi(): com.fruex.beerwall.ui.models.Transaction {
    return com.fruex.beerwall.ui.models.Transaction(
        transactionId = transactionId,
        commodityName = commodityName,
        startDateTime = startDateTime,
        grossPrice = grossPrice,
        capacity = capacity
    )
}

fun List<Transaction>.toUi(): List<com.fruex.beerwall.ui.models.Transaction> {
    return map { it.toUi() }
}

fun List<Transaction>.groupByDate(): List<DailyTransactions> {
    return groupBy { it.startDateTime.substringBefore("T") }
        .map { (dateString, transactions) ->
            val formattedDate = try {
                val date = LocalDate.parse(dateString)
                "${date.day} ${monthNames[date.month.number - 1]}"
            } catch (e: Exception) {
                dateString.uppercase()
            }

            DailyTransactions(
                date = formattedDate,
                transactions = transactions.toUi()
            )
        }
}
