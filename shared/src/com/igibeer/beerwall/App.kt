package com.igibeer.beerwall

import androidx.compose.runtime.Composable
import com.igibeer.beerwall.ui.models.*
import com.igibeer.beerwall.ui.navigation.BeerWallNavHost
import com.igibeer.beerwall.ui.theme.BeerWallTheme

@Composable
fun App(
    onGoogleSignIn: () -> Unit = {},
    onStartNfcScanning: () -> Unit = {},
    scannedCardId: String? = null,
    isNfcScanning: Boolean = false,
    onSaveCard: (name: String, cardId: String) -> Unit = { _, _ -> },
    onRegister: (email: String, password: String) -> Unit = { _, _ -> },
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onLogout: () -> Unit = {},
    onAddFunds: (location: String, amount: Double) -> Unit = { _, _ -> },
    onToggleCardStatus: (String) -> Unit = {},
    onDeleteCard: (String) -> Unit = {}
) {
    BeerWallTheme {
        BeerWallNavHost(
            balances = emptyList(),
            cards = emptyList(),
            transactionGroups = emptyList(),
            userProfile = null,
            onGoogleSignIn = onGoogleSignIn,
            onStartNfcScanning = onStartNfcScanning,
            scannedCardId = scannedCardId,
            isNfcScanning = isNfcScanning,
            onSaveCard = onSaveCard,
            onRegister = onRegister,
            onLogin = onLogin,
            onLogout = onLogout,
            onAddFunds = onAddFunds,
            onToggleCardStatus = onToggleCardStatus,
            onDeleteCard = onDeleteCard
        )
    }
}
