package com.fruex.beerwall.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface NfcRepository {
    val scannedCardId: StateFlow<String?>
    val isNfcEnabled: StateFlow<Boolean>

    suspend fun setScannedCardId(cardId: String?)
    suspend fun setNfcEnabled(isEnabled: Boolean)
    suspend fun clearScannedCard()
}
