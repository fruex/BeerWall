package org.fruex.beerwall.presentation.mapper

import org.fruex.beerwall.domain.model.Transaction
import org.fruex.beerwall.ui.models.DailyTransactions

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
        .map { (date, transactions) ->
            DailyTransactions(
                date = date.uppercase(),
                transactions = transactions.toUi()
            )
        }
}
