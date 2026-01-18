package org.fruex.beerwall

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    App(
        scannedCardId = null,
        isNfcEnabled = true, // Na iOS NFC jest obsługiwane inaczej, zakładamy true lub później dodamy obsługę
        onStartNfcScanningClick = {}
    ) 
}