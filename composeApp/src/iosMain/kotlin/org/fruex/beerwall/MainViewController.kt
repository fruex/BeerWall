package org.fruex.beerwall

import androidx.compose.ui.window.ComposeUIViewController
import org.fruex.beerwall.di.initKoin

fun MainViewController() = ComposeUIViewController {
    // Initialize Koin only once
    // Note: In a real iOS app, this might be better placed in the Swift AppDelegate
    // but this ensures it runs for the Compose view.
    try {
        initKoin()
    } catch (e: Exception) {
        // Ignore if already started (e.g. by Swift code or previous call)
    }

    App(
        scannedCardId = null,
        isNfcEnabled = true, // Na iOS NFC jest obsługiwane inaczej, zakładamy true lub później dodamy obsługę
        onStartNfcScanningClick = {}
    ) 
}