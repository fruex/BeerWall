package com.fruex.beerwall

import androidx.compose.ui.window.ComposeUIViewController
import com.fruex.beerwall.nfc.NfcManager

fun MainViewController() = ComposeUIViewController {
    // Initialize NFC Manager when the UI starts
    // In a real iOS app lifecycle, we might want this in AppDelegate,
    // but here is a safe place for Compose Multiplatform to ensure dependencies are ready.
    NfcManager.initialize()

    App()
}