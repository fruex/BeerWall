package org.fruex.beerwall

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.fruex.beerwall.auth.rememberGoogleAuthProvider
import org.fruex.beerwall.remote.BeerWallApiClient
import org.fruex.beerwall.ui.navigation.BeerWallNavHost
import org.fruex.beerwall.ui.navigation.NavigationDestination
import org.fruex.beerwall.ui.theme.BeerWallTheme

@Composable
@Preview
fun App(
    scannedCardId: String? = null,
    onStartNfcScanning: () -> Unit = {}
) {
    val viewModel: BeerWallViewModel = viewModel {
        BeerWallViewModel(apiClient = BeerWallApiClient())
    }
    val uiState by viewModel.uiState.collectAsState()

    val googleAuthProvider = rememberGoogleAuthProvider()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onClearError()
        }
    }

    LaunchedEffect(Unit) {
        val user = googleAuthProvider.getSignedInUser()
        viewModel.onSessionCheckComplete(user)
    }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            viewModel.refreshAllData()
        }
    }

    if (uiState.isCheckingSession) {
        return
    }

    BeerWallTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            BeerWallNavHost(
                modifier = Modifier.padding(paddingValues),
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
                onLogin = { _, _ -> viewModel.setGuestSession() },
                onRegister = { _, _ -> viewModel.setGuestSession() },
                onGoogleSignIn = { onSuccess ->
                    scope.launch {
                        googleAuthProvider.signIn()?.let { user ->
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
}

