package org.fruex.beerwall.presentation.mapper

import org.fruex.beerwall.domain.model.Transaction
import org.fruex.beerwall.ui.models.DailyTransactions

fun Transaction.toUi(): org.fruex.beerwall.ui.models.Transaction {
    return org.fruex.beerwall.ui.models.Transaction(
        id = id,
        beerName = beerName,
        date = date,
        time = time,
        amount = amount,
        cardNumber = cardNumber
    )
}

fun List<Transaction>.toUi(): List<org.fruex.beerwall.ui.models.Transaction> {
    return map { it.toUi() }
}

fun List<Transaction>.groupByDate(): List<DailyTransactions> {
    return groupBy { it.date }
        .map { (date, transactions) ->
            DailyTransactions(
                date = date.uppercase(),
                transactions = transactions.toUi()
            )
        }
}
