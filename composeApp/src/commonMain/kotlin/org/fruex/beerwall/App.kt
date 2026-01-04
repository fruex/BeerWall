package org.fruex.beerwall

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.fruex.beerwall.auth.rememberGoogleAuthProvider
import org.fruex.beerwall.ui.BeerWallUiState
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
    val viewModel: BeerWallViewModel = viewModel { BeerWallViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    val googleAuthProvider = rememberGoogleAuthProvider()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        println("App: Checking session...")
        val user = googleAuthProvider.getSignedInUser()
        println("App: Session user: ${user?.email ?: "null"}")
        viewModel.onSessionCheckComplete(user)
    }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            viewModel.refreshAllData()
        }
    }

    if (uiState.isCheckingSession) {
        // We could show a splash screen here, but for now we just wait
        return
    }

    BeerWallTheme {
        BeerWallNavHost(
            startDestination = if (uiState.isLoggedIn) NavigationDestination.Main.route else NavigationDestination.Login.route,
            balances = uiState.balances,
            cards = uiState.cards,
            transactionGroups = uiState.transactionGroups,
            userProfile = uiState.userProfile,
            isRefreshing = uiState.isRefreshing,
            scannedCardId = scannedCardId,
            onStartNfcScanning = onStartNfcScanning,
            onRefreshHistory = viewModel::refreshHistory,
            onRefreshBalance = viewModel::refreshBalance,
            onLogin = { _, _ ->
                 viewModel.setGuestSession()
            },
            onRegister = { _, _ -> 
                 viewModel.setGuestSession()
            },
            onGoogleSignIn = { onSuccess ->
                scope.launch {
                    val user = googleAuthProvider.signIn()
                    if (user != null) {
                        viewModel.onLoginSuccess(user)
                        onSuccess()
                    }
                }
            },
            onAddFunds = viewModel::onAddFunds,
            onToggleCardStatus = viewModel::onToggleCardStatus,
            onDeleteCard = viewModel::onDeleteCard,
            onSaveCard = viewModel::onSaveCard,
            onLogout = {
                scope.launch {
                    googleAuthProvider.signOut()
                    viewModel.onLogout()
                }
            }
        )
    }
}
