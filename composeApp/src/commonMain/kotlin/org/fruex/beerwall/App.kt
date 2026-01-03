package org.fruex.beerwall

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import org.fruex.beerwall.auth.rememberGoogleAuthProvider
import org.fruex.beerwall.remote.BeerWallApiClient
import org.fruex.beerwall.ui.models.*
import org.fruex.beerwall.ui.navigation.BeerWallNavHost
import org.fruex.beerwall.ui.navigation.NavigationDestination
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    scannedCardId: String? = null,
    onStartNfcScanning: () -> Unit = {}
) {
    var isCheckingSession by remember { mutableStateOf(true) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var balances by remember { mutableStateOf(emptyList<LocationBalance>()) }
    var cards by remember { mutableStateOf(emptyList<CardItem>()) }
    var userProfile by remember { mutableStateOf(SampleUserProfile.copy(activeCards = 0, loyaltyPoints = 0)) }
    var transactionGroups by remember { mutableStateOf(emptyList<TransactionGroup>()) }
    val googleAuthProvider = rememberGoogleAuthProvider()
    val apiClient = remember { BeerWallApiClient() }
    val scope = rememberCoroutineScope()

    fun refreshAllData() {
        scope.launch {
            apiClient.getBalance().onSuccess { balances = it }
            apiClient.getCards().onSuccess { 
                cards = it
                userProfile = userProfile.copy(activeCards = it.count { card -> card.isActive })
            }
            apiClient.getHistory().onSuccess { transactions ->
                transactionGroups = transactions
                    .groupBy { it.date }
                    .map { (date, items) -> TransactionGroup(date.uppercase(), items) }
            }
            apiClient.getProfile().onSuccess { points ->
                userProfile = userProfile.copy(loyaltyPoints = points)
            }
        }
    }

    LaunchedEffect(Unit) {
        println("App: Checking session...")
        val user = googleAuthProvider.getSignedInUser()
        println("App: Session user: ${user?.email ?: "null"}")
        if (user != null) {
            userProfile = userProfile.copy(
                name = user.displayName ?: userProfile.name,
                email = user.email ?: userProfile.email,
                initials = user.displayName?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: userProfile.initials,
                photoUrl = user.photoUrl
            )
            isLoggedIn = true
            refreshAllData()
        }
        isCheckingSession = false
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            refreshAllData()
        }
    }

    if (isCheckingSession) {
        // We could show a splash screen here, but for now we just wait
        return
    }

    BeerWallTheme {
        BeerWallNavHost(
            startDestination = if (isLoggedIn) NavigationDestination.Main.route else NavigationDestination.Login.route,
            balances = balances,
            cards = cards,
            transactionGroups = transactionGroups,
            userProfile = userProfile,
            scannedCardId = scannedCardId,
            onStartNfcScanning = onStartNfcScanning,
            onLogin = { _, _ -> 
                isLoggedIn = true
                refreshAllData()
            },
            onRegister = { _, _ -> 
                isLoggedIn = true
                refreshAllData()
            },
            onGoogleSignIn = { onSuccess ->
                scope.launch {
                    val user = googleAuthProvider.signIn()
                    if (user != null) {
                        userProfile = userProfile.copy(
                            name = user.displayName ?: userProfile.name,
                            email = user.email ?: userProfile.email,
                            initials = user.displayName?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: userProfile.initials,
                            photoUrl = user.photoUrl
                        )
                        isLoggedIn = true
                        refreshAllData()
                        onSuccess()
                    }
                }
            },
            onAddFunds = { location, amount ->
                balances = balances.map {
                    if (it.venueName == location) {
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
                    isLoggedIn = false
                }
            }
        )
    }
}