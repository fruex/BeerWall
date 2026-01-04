package org.fruex.beerwall

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    var isCheckingSession by rememberSaveable { mutableStateOf(true) }
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var balances by remember { mutableStateOf(emptyList<VenueBalance>()) }
    var cards by remember { mutableStateOf(emptyList<UserCard>()) }
    var userProfile by remember { mutableStateOf(UserProfile(name = "", email = "", initials = "", activeCards = 0, loyaltyPoints = 0)) }
    var transactionGroups by remember { mutableStateOf(emptyList<DailyTransactions>()) }
    val googleAuthProvider = rememberGoogleAuthProvider()
    val apiClient = remember { BeerWallApiClient() }
    val scope = rememberCoroutineScope()

    fun refreshAllData() {
        scope.launch {
            isRefreshing = true
            
            val balanceDeferred = async { apiClient.getBalance() }
            val cardsDeferred = async { apiClient.getCards() }
            val historyDeferred = async { apiClient.getHistory() }
            val profileDeferred = async { apiClient.getProfile() }

            val balanceResult = balanceDeferred.await()
            val cardsResult = cardsDeferred.await()
            val historyResult = historyDeferred.await()
            val profileResult = profileDeferred.await()

            balanceResult.onSuccess { balances = it }
            cardsResult.onSuccess { 
                cards = it
                userProfile = userProfile.copy(activeCards = it.count { card -> card.isActive })
            }
            historyResult.onSuccess { transactions ->
                transactionGroups = transactions
                    .groupBy { it.date }
                    .map { (date, items) -> DailyTransactions(date.uppercase(), items) }
            }
            profileResult.onSuccess { points ->
                userProfile = userProfile.copy(loyaltyPoints = points)
            }
            
            isRefreshing = false
        }
    }

    fun onAddFunds(venueName: String, amount: Double) {
        scope.launch {
            apiClient.topUp(amount, venueName).onSuccess { newBalance ->
                balances = balances.map {
                    if (it.venueName == venueName) {
                        it.copy(balance = newBalance)
                    } else it
                }
            }.onFailure {
                // Here we could show an error
                println("Failed to top up: ${it.message}")
            }
        }
    }

    fun onToggleCardStatus(cardId: String) {
        val card = cards.find { it.id == cardId } ?: return
        scope.launch {
            apiClient.toggleCardStatus(cardId, !card.isActive).onSuccess { isActive ->
                cards = cards.map {
                    if (it.id == cardId) {
                        it.copy(isActive = isActive)
                    } else it
                }
                userProfile = userProfile.copy(activeCards = cards.count { it.isActive })
            }
        }
    }

    fun refreshHistory() {
        scope.launch {
            isRefreshing = true
            apiClient.getHistory().onSuccess { transactions ->
                transactionGroups = transactions
                    .groupBy { it.date }
                    .map { (date, items) -> DailyTransactions(date.uppercase(), items) }
            }
            isRefreshing = false
        }
    }

    fun refreshBalance() {
        scope.launch {
            isRefreshing = true
            apiClient.getBalance().onSuccess { balances = it }
            isRefreshing = false
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
            isRefreshing = isRefreshing,
            scannedCardId = scannedCardId,
            onStartNfcScanning = onStartNfcScanning,
            onRefreshHistory = ::refreshHistory,
            onRefreshBalance = ::refreshBalance,
            onLogin = { _, _ -> 
                isLoggedIn = true
            },
            onRegister = { _, _ -> 
                isLoggedIn = true
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
                        onSuccess()
                    }
                }
            },
            onAddFunds = ::onAddFunds,
            onToggleCardStatus = ::onToggleCardStatus,
            onDeleteCard = { cardId ->
                cards = cards.filter { it.id != cardId || !it.isPhysical }
                userProfile = userProfile.copy(activeCards = cards.count { it.isActive })
            },
            onSaveCard = { name, cardId ->
                val newCard = UserCard(
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