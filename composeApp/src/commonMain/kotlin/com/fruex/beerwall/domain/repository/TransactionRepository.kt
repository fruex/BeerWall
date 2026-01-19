package com.fruex.beerwall.domain.repository

import com.fruex.beerwall.domain.model.Transaction

/**
 * Interfejs repozytorium do pobierania historii transakcji.
 */
interface TransactionRepository {
    /**
     * Pobiera historię transakcji użytkownika.
     *
     * @return [Result] zawierający listę obiektów [Transaction] lub błąd.
     */
    suspend fun getTransactions(): Result<List<Transaction>>
}
