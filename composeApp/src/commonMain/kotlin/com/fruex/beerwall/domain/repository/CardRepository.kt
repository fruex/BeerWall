package com.fruex.beerwall.domain.repository

import com.fruex.beerwall.domain.model.Card

/**
 * Interfejs repozytorium kart.
 */
interface CardRepository {
    /**
     * Pobiera listę kart użytkownika.
     *
     * @return [Result] zawierający listę kart lub błąd.
     */
    suspend fun getCards(): Result<List<Card>>

    /**
     * Aktualizuje dane karty (status aktywności, opis).
     *
     * @param cardId Identyfikator karty.
     * @param description Opis karty.
     * @param isActive Nowy status aktywności (true = aktywna).
     * @return [Result] pusty w przypadku sukcesu lub błąd.
     */
    suspend fun updateCard(cardId: String, description: String, isActive: Boolean): Result<Unit>

    /**
     * Przypisuje nową kartę do użytkownika.
     *
     * @param guid GUID karty.
     * @param description Opis karty.
     * @return [Result] pusty w przypadku sukcesu lub błąd.
     */
    suspend fun assignCard(guid: String, description: String): Result<Unit>

    /**
     * Usuwa kartę użytkownika.
     *
     * @param guid GUID karty.
     * @return [Result] pusty w przypadku sukcesu lub błąd.
     */
    suspend fun deleteCard(guid: String): Result<Unit>
}
