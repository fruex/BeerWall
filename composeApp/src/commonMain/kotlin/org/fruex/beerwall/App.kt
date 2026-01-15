package org.fruex.beerwall

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.fruex.beerwall.auth.rememberGoogleAuthProvider
import org.fruex.beerwall.di.createAppContainer
import org.fruex.beerwall.presentation.BeerWallViewModel
import org.fruex.beerwall.ui.navigation.BeerWallNavHost
import org.fruex.beerwall.ui.navigation.NavigationDestination
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.GoldPrimary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    scannedCardId: String? = null,
    isNfcEnabled: Boolean = true,
    onStartNfcScanning: () -> Unit = {}
) {
    val appContainer = createAppContainer()
    val viewModel: BeerWallViewModel = viewModel {
        appContainer.createBeerWallViewModel()
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
        // Sprawdź czy użytkownik ma zapisany token .NET
        viewModel.checkSession()
    }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            viewModel.refreshAllData()
        }
    }

    BeerWallTheme {
        if (uiState.isCheckingSession) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { paddingValues ->
                BeerWallNavHost(
                    modifier = Modifier.padding(paddingValues),
                    startDestination =
                        if (uiState.isLoggedIn)
                            NavigationDestination.Main.route
                        else NavigationDestination.Login.route,
                    balances = uiState.balances,
                    cards = uiState.cards,
                    transactionGroups = uiState.transactionGroups,
                    userProfile = uiState.userProfile,
                    paymentMethods = uiState.paymentMethods,
                    isRefreshing = uiState.isRefreshing,
                    scannedCardId = scannedCardId,
                    isNfcEnabled = isNfcEnabled,
                    onStartNfcScanning = onStartNfcScanning,
                    onRefreshHistory = viewModel::refreshHistory,
                    onRefreshBalance = viewModel::refreshBalance,
                    onRegisterWithEmail = viewModel::handleRegister,
                    onLoginWithEmail = viewModel::handleEmailPasswordSignIn,
                    onLoginWithGoogle = {
                        viewModel.handleGoogleSignIn(googleAuthProvider)
                    },
                    onAddFunds = viewModel::onAddFunds,
                    onToggleCardStatus = viewModel::onToggleCardStatus,
                    onDeleteCard = viewModel::onDeleteCard,
                    onSaveCard = viewModel::onSaveCard,
                    onForgotPassword = viewModel::handleForgotPassword,
                    onResetPassword = viewModel::handleResetPassword,
                    onLogout = {
                        viewModel.handleLogout(googleAuthProvider)
                    }
                )
            }
        }
    }
}
