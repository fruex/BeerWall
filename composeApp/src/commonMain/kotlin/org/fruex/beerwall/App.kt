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
import org.fruex.beerwall.auth.rememberGoogleAuthProvider
import org.fruex.beerwall.di.createAppContainer
import org.fruex.beerwall.presentation.AppViewModel
import org.fruex.beerwall.ui.navigation.AppNavHost
import org.fruex.beerwall.ui.navigation.NavigationDestination
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.GoldPrimary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    scannedCardId: String? = null,
    isNfcEnabled: Boolean = true,
    onStartNfcScanningClick: () -> Unit = {}
) {
    val appContainer = createAppContainer()
    val viewModel: AppViewModel = viewModel {
        appContainer.createBeerWallViewModel()
    }
    val uiState by viewModel.uiState.collectAsState()

    val googleAuthProvider = rememberGoogleAuthProvider()
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
                AppNavHost(
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
                    onStartNfcScanningClick = onStartNfcScanningClick,
                    onRefreshHistoryClick = viewModel::refreshHistory,
                    onRefreshBalanceClick = viewModel::refreshBalance,
                    onRegisterWithEmail = viewModel::handleRegister,
                    onLoginWithEmail = viewModel::handleEmailPasswordSignIn,
                    onLoginWithGoogleClick = {
                        viewModel.handleGoogleSignIn(googleAuthProvider)
                    },
                    onAddFundsClick = viewModel::onAddFunds,
                    onToggleCardStatusClick = viewModel::onToggleCardStatus,
                    onDeleteCardClick = viewModel::onDeleteCard,
                    onSaveCardClick = viewModel::onSaveCard,
                    onForgotPassword = viewModel::handleForgotPassword,
                    onResetPassword = { email, resetCode, newPassword ->
                        viewModel.handleResetPassword(email, resetCode, newPassword)
                    },
                    onSendMessage = viewModel::onSendMessage,
                    onLogoutClick = {
                        viewModel.handleLogout(googleAuthProvider)
                    }
                )
            }
        }
    }
}
