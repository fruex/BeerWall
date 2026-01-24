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
    val time = startDateTime.time
    val formattedTime = "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"

    return com.fruex.beerwall.ui.models.Transaction(
        transactionId = transactionId,
        commodityName = commodityName,
        formattedPrice = "$grossPrice zł",
        formattedCapacity = "$capacity ml",
        formattedDetails = "$premisesName o $formattedTime"
    )
}

fun List<Transaction>.toUi(): List<com.fruex.beerwall.ui.models.Transaction> {
    return map { it.toUi() }
}

fun List<Transaction>.groupByDate(): List<DailyTransactions> {
    return groupBy { it.startDateTime.date }
        .map { (date, transactions) ->
            val formattedDate = "${date.day} ${monthNames[date.month.number - 1]}"

            DailyTransactions(
                date = formattedDate,
                transactions = transactions.toUi()
            )
        }
}
