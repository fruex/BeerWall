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
import org.fruex.beerwall.presentation.viewmodel.AuthViewModel
import org.fruex.beerwall.ui.navigation.AppNavHost
import org.fruex.beerwall.ui.navigation.NavigationDestination
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.GoldPrimary
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    scannedCardId: String? = null,
    isNfcEnabled: Boolean = true,
    onStartNfcScanningClick: () -> Unit = {}
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val authState by authViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        authViewModel.checkSession()
    }

    LaunchedEffect(authState.errorMessage) {
        authState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            authViewModel.onClearError()
        }
    }

    BeerWallTheme {
        if (authState.isCheckingSession) {
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
                        if (authState.isLoggedIn)
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
