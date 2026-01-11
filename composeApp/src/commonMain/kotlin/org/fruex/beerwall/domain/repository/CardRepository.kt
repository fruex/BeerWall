package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.domain.model.Card

/**
 * Interfejs repozytorium do zarządzania kartami RFID użytkownika.
 */
interface CardRepository {
    /**
     * Pobiera listę kart przypisanych do użytkownika.
     * @return Result z listą obiektów [Card].
     */
    suspend fun getCards(): Result<List<Card>>

    /**
     * Zmienia status aktywności karty (blokowanie/odblokowywanie).
     * @param cardId ID karty.
     * @param isActive Nowy status (true - aktywna).
     * @return Result zawierający nowy status karty (boolean).
     */
    suspend fun toggleCardStatus(cardId: String, isActive: Boolean): Result<Boolean>
}
