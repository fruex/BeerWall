package com.fruex.beerwall.data.mapper

import com.fruex.beerwall.domain.model.Transaction
import com.fruex.beerwall.data.remote.dto.history.TransactionResponse
import kotlinx.datetime.LocalDateTime

/**
 * Mapuje [TransactionResponse] (DTO) na [Transaction] (Domain Model).
 */
fun TransactionResponse.toDomain(): Transaction {
    return Transaction(
        transactionId = transactionId,
        commodityName = commodityName,
        startDateTime = LocalDateTime.parse(startDateTime),
        grossPrice = grossPrice,
        capacity = capacity,
        premisesName = premisesName
    )
}

/**
 * Mapuje listę [TransactionResponse] na listę [Transaction].
 */
fun List<TransactionResponse>.toDomain(): List<Transaction> {
    return map { it.toDomain() }
}
