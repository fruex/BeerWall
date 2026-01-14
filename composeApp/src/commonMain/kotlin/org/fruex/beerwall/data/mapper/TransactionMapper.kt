package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.domain.model.Transaction
import org.fruex.beerwall.remote.dto.history.TransactionResponse

fun TransactionResponse.toDomain(): Transaction {
    return Transaction(
        transactionId = transactionId,
        commodityName = commodityName,
        startDateTime = startDateTime,
        grossPrice = grossPrice,
        capacity = capacity
    )
}

fun List<TransactionResponse>.toDomain(): List<Transaction> {
    return map { it.toDomain() }
}
