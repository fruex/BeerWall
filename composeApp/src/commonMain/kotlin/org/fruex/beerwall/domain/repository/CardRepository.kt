package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.domain.model.Card

/**
 * Interfejs repozytorium do zarządzania kartami użytkownika.
 */
interface CardRepository {
    /**
     * Pobiera listę kart przypisanych do użytkownika.
     *
     * @return [Result] zawierający listę obiektów [Card] lub błąd.
     */
    suspend fun getCards(): Result<List<Card>>

    /**
     * Zmienia status aktywności karty (blokuje/odblokowuje).
     *
     * @param cardId Identyfikator karty.
     * @param isActive Nowy status aktywności (true = aktywna).
     * @return [Result] zawierający nowy status lub błąd.
     */
    suspend fun toggleCardStatus(cardId: String, isActive: Boolean): Result<Boolean>

    /**
     * Przypisuje nową kartę do konta użytkownika.
     *
     * @param guid Unikalny identyfikator karty (GUID).
     * @param description Opis karty nadany przez użytkownika.
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend fun assignCard(guid: String, description: String): Result<Unit>

    /**
     * Usuwa kartę z konta użytkownika.
     *
     * @param guid Unikalny identyfikator karty (GUID) do usunięcia.
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend fun deleteCard(guid: String): Result<Unit>
}
