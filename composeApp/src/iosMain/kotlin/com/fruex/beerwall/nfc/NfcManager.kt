package com.fruex.beerwall.nfc

import com.fruex.beerwall.domain.repository.NfcRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object NfcManager : KoinComponent {
    private val nfcRepository: NfcRepository by inject()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val scanner by lazy { IosNfcScanner(nfcRepository) }

    fun initialize() {
        scope.launch {
            nfcRepository.isScanning.collect { isScanning ->
                if (isScanning) {
                    scanner.startScanning()
                } else {
                    scanner.stopScanning()
                }
            }
        }

        // On iOS, we assume NFC is enabled if the device supports it.
        // CoreNFC checks are implicit when session starts.
        // We set it to true to allow UI to show scanning state.
        scope.launch {
            nfcRepository.setNfcEnabled(true)
        }
    }
}
