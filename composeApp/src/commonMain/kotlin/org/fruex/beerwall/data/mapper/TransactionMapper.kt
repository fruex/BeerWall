package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.domain.model.Transaction
import org.fruex.beerwall.remote.dto.history.TransactionResponse

fun TransactionResponse.toDomain(): Transaction {
    return Transaction(
        id = id,
        beverageName = beverageName,
        timestamp = timestamp,
        venueName = venueName,
        amount = amount,
        volumeMilliliters = volumeMilliliters
    )
}

fun List<TransactionResponse>.toDomain(): List<Transaction> {
    return map { it.toDomain() }
}
