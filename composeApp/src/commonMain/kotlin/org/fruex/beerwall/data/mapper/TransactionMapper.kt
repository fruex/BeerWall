package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.domain.model.Transaction
import org.fruex.beerwall.remote.dto.history.TransactionResponse

/**
 * Mapuje [TransactionResponse] (DTO) na [Transaction] (Domain Model).
 */
fun TransactionResponse.toDomain(): Transaction {
    return Transaction(
        transactionId = transactionId,
        commodityName = commodityName,
        startDateTime = startDateTime,
        grossPrice = grossPrice,
        capacity = capacity
    )
}

/**
 * Mapuje listę [TransactionResponse] na listę [Transaction].
 */
fun List<TransactionResponse>.toDomain(): List<Transaction> {
    return map { it.toDomain() }
}
