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
import org.fruex.beerwall.auth.SessionManager
import org.fruex.beerwall.presentation.viewmodel.AuthViewModel
import org.fruex.beerwall.presentation.viewmodel.BalanceViewModel
import org.fruex.beerwall.presentation.viewmodel.CardsViewModel
import org.fruex.beerwall.presentation.viewmodel.HistoryViewModel
import org.fruex.beerwall.ui.navigation.AppNavHost
import org.fruex.beerwall.ui.navigation.NavigationDestination
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.GoldPrimary
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    scannedCardId: String? = null,
    isNfcEnabled: Boolean = true,
    onStartNfcScanningClick: () -> Unit = {}
) {
    // In Koin 4.0, we rely on KoinContext usually set up at the app entry point.
    // If not, we could wrap this in KoinContext { ... }

    val authViewModel = koinViewModel<AuthViewModel>()
    val sessionManager = koinInject<SessionManager>() // Global session manager

    val uiState by authViewModel.uiState.collectAsState()
    val isSessionExpired by sessionManager.isSessionExpired.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Initial session check
    LaunchedEffect(Unit) {
        authViewModel.checkSession()
    }

    // Handle session expiration
    val googleAuthProvider = org.fruex.beerwall.auth.rememberGoogleAuthProvider()

    LaunchedEffect(isSessionExpired) {
        if (isSessionExpired) {
            authViewModel.handleLogout(googleAuthProvider)
            sessionManager.resetSession()
            snackbarHostState.showSnackbar("Sesja wygasła. Zaloguj się ponownie.")
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            authViewModel.onClearError()
        }
    }

    // Global error handling from other viewmodels can be aggregated here via a MessageBus or similar,
    // or handled locally in screens. For now, we focus on Auth errors.

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
                    scannedCardId = scannedCardId,
                    isNfcEnabled = isNfcEnabled,
                    onStartNfcScanningClick = onStartNfcScanningClick
                )
            }
        }
    }
}
