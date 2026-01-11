package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.domain.model.Transaction

/**
 * Interfejs repozytorium do obsługi historii transakcji.
 */
interface TransactionRepository {
    /**
     * Pobiera historię transakcji użytkownika.
     * @return Result z listą obiektów [Transaction].
     * // TODO: Dodać obsługę stronicowania (pagination) lub filtrowania po dacie, jeśli historia może być długa.
     */
    suspend fun getTransactions(): Result<List<Transaction>>
}
