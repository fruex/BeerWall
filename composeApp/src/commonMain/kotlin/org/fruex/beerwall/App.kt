package org.fruex.beerwall

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
    var balances by remember { mutableStateOf(SampleBalances) }
    var cards by remember { mutableStateOf(emptyList<CardItem>()) }
    var userProfile by remember { mutableStateOf(SampleUserProfile) }
    var transactionGroups by remember { mutableStateOf(emptyList<TransactionGroup>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val googleAuthProvider = rememberGoogleAuthProvider()
    val apiClient = remember { BeerWallApiClient() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val user = googleAuthProvider.getSignedInUser()
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
            apiClient.getBalance()
                .onSuccess { balances = it }
                .onFailure { errorMessage = it.message }
            apiClient.getCards()
                .onSuccess { cards = it }
                .onFailure { errorMessage = it.message }
            apiClient.getTransactions()
                .onSuccess { transactionGroups = it }
                .onFailure { errorMessage = it.message }
        }
    }

    if (isCheckingSession) {
        // We could show a splash screen here, but for now we just wait
        return
    }

    BeerWallTheme {
        Column {
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            BeerWallNavHost(
                startDestination = if (isLoggedIn) NavigationDestination.Main.route else NavigationDestination.Login.route,
                balances = balances,
            cards = cards,
            transactionGroups = transactionGroups,
            userProfile = userProfile,
            scannedCardId = scannedCardId,
            onStartNfcScanning = onStartNfcScanning,
            onLogin = { _, _ -> isLoggedIn = true },
            onRegister = { _, _ -> isLoggedIn = true },
            onGoogleSignIn = { onSuccess ->
                scope.launch {
                    val user = googleAuthProvider.signIn()
                    if (user != null) {
                        userProfile = userProfile.copy(
                            name = user.displayName ?: user-profile.name,
                            email = user.email ?: user-profile.email,
                            initials = user.displayName?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: user-profile.initials,
                            photoUrl = user.photoUrl
                        )
                        isLoggedIn = true
                        onSuccess()
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
                    isLoggedIn = false
                }
            }
        )
        }
    }
}
