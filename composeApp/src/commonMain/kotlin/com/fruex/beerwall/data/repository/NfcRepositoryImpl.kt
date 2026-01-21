package com.fruex.beerwall.data.repository

import com.fruex.beerwall.domain.repository.NfcRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NfcRepositoryImpl : NfcRepository {

    private val _scannedCardId = MutableStateFlow<String?>(null)
    override val scannedCardId: StateFlow<String?> = _scannedCardId.asStateFlow()

    private val _isNfcEnabled = MutableStateFlow(false)
    override val isNfcEnabled: StateFlow<Boolean> = _isNfcEnabled.asStateFlow()

    override suspend fun setScannedCardId(cardId: String?) {
        _scannedCardId.value = cardId
    }

    override suspend fun setNfcEnabled(isEnabled: Boolean) {
        _isNfcEnabled.value = isEnabled
    }

    override suspend fun clearScannedCard() {
        _scannedCardId.value = null
    }
}
