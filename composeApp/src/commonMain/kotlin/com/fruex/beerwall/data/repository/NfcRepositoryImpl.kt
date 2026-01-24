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

    private val _isScanning = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    override suspend fun setScannedCardId(cardId: String?) {
        _scannedCardId.value = cardId
    }

    override suspend fun setNfcEnabled(isEnabled: Boolean) {
        _isNfcEnabled.value = isEnabled
    }

    override suspend fun setScanning(isActive: Boolean) {
        _isScanning.value = isActive
    }

    override suspend fun clearScannedCard() {
        _scannedCardId.value = null
    }
}
