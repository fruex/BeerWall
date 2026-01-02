package org.fruex.beerwall

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import org.fruex.beerwall.auth.rememberGoogleAuthProvider
import org.fruex.beerwall.ui.models.*
import org.fruex.beerwall.ui.navigation.BeerWallNavHost
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    scannedCardId: String? = null,
    onStartNfcScanning: () -> Unit = {}
) {
    var balances by remember { mutableStateOf(SampleBalances) }
    var cards by remember { mutableStateOf(SampleCards) }
    var userProfile by remember { mutableStateOf(SampleUserProfile) }
    val transactionGroups by remember { mutableStateOf(SampleTransactionGroups) }
    val googleAuthProvider = rememberGoogleAuthProvider()
    val scope = rememberCoroutineScope()

    BeerWallTheme {
        BeerWallNavHost(
            balances = balances,
            cards = cards,
            transactionGroups = transactionGroups,
            userProfile = userProfile,
            scannedCardId = scannedCardId,
            onStartNfcScanning = onStartNfcScanning,
            onGoogleSignIn = {
                scope.launch {
                    val user = googleAuthProvider.signIn()
                    if (user != null) {
                        userProfile = userProfile.copy(
                            name = user.displayName ?: userProfile.name,
                            email = user.email ?: userProfile.email,
                            initials = user.displayName?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: userProfile.initials
                        )
                    }
                }
            },
            onAddFunds = { location, amount ->
                balances = balances.map {
                    if (it.locationName == location) {
                        it.copy(balance = it.balance + amount)
                    } else it
                }
            },
            onToggleCardStatus = { cardId ->
                cards = cards.map {
                    if (it.id == cardId) {
                        it.copy(isActive = !it.isActive)
                    } else it
                }
            },
            onDeleteCard = { cardId ->
                cards = cards.filter { it.id != cardId }
                userProfile = userProfile.copy(activeCards = cards.count { it.isActive })
            },
            onSaveCard = { name, cardId ->
                val newCard = CardItem(
                    id = cardId,
                    name = name,
                    isActive = true,
                    isPhysical = true
                )
                cards = cards + newCard
                userProfile = userProfile.copy(activeCards = cards.count { it.isActive })
            },
            onLogout = {
                scope.launch {
                    googleAuthProvider.signOut()
                }
            }
        )
    }
}