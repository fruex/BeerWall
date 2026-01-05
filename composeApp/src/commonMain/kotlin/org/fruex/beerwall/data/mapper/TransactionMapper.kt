package org.fruex.beerwall.data.mapper

import org.fruex.beerwall.domain.model.Transaction
import org.fruex.beerwall.remote.dto.history.TransactionDto

fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        beerName = beerName,
        date = date,
        time = time,
        amount = amount,
        cardNumber = cardNumber
    )
}

fun List<TransactionDto>.toDomain(): List<Transaction> {
    return map { it.toDomain() }
}
