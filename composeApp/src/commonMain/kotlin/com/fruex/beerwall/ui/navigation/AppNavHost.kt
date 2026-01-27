package com.fruex.beerwall.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fruex.beerwall.ui.auth.rememberGoogleAuthProvider
import com.fruex.beerwall.presentation.viewmodel.AuthViewModel
import com.fruex.beerwall.presentation.viewmodel.BalanceViewModel
import com.fruex.beerwall.presentation.viewmodel.CardsViewModel
import com.fruex.beerwall.ui.screens.auth.AuthMode
import com.fruex.beerwall.ui.screens.auth.AuthScreen
import com.fruex.beerwall.ui.screens.balance.AddFundsScreen
import com.fruex.beerwall.ui.screens.cards.AddCardScreen
import org.koin.compose.viewmodel.koinViewModel

/**
 * Główny komponent nawigacyjny aplikacji (NavHost).
 *
 * Definiuje graf nawigacji i obsługuje przejścia między ekranami.
 *
 * @param modifier Modyfikator układu.
 * @param navController Kontroler nawigacji.
 * @param startDestination Punkt startowy nawigacji.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationDestination.Main.route
) {
    val googleAuthProvider = rememberGoogleAuthProvider()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth screens
        composable(NavigationDestination.Registration.route) {
            val authViewModel = koinViewModel<AuthViewModel>()
            val uiState by authViewModel.uiState.collectAsState()

            AuthScreen(
                mode = AuthMode.REGISTER,
                onAuthClick = { email, password ->
                    authViewModel.handleRegister(email, password)
                },
                onGoogleSignInClick = {
                    authViewModel.handleGoogleSignIn(googleAuthProvider)
                },
                onToggleModeClick = {
                    navController.navigate(NavigationDestination.Login.route) {
                        popUpTo(NavigationDestination.Registration.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage
            )
        }

        composable(NavigationDestination.Login.route) {
            val authViewModel = koinViewModel<AuthViewModel>()
            val uiState by authViewModel.uiState.collectAsState()

            AuthScreen(
                mode = AuthMode.LOGIN,
                onAuthClick = { email, password ->
                    authViewModel.handleEmailPasswordSignIn(email, password)
                },
                onGoogleSignInClick = {
                    authViewModel.handleGoogleSignIn(googleAuthProvider)
                },
                onToggleModeClick = {
                    navController.navigate(NavigationDestination.Registration.route) {
                        popUpTo(NavigationDestination.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = { email ->
                    authViewModel.handleForgotPassword(email)
                },
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage
            )
        }

        // Main screen with bottom navigation
        composable(NavigationDestination.Main.route) {
            MainScreen(
                onAddFundsClick = { premisesId ->
                    navController.navigate("${NavigationDestination.AddFunds.route}/$premisesId") {
                        launchSingleTop = true
                    }
                },
                onAddLocationClick = {
                    navController.navigate(NavigationDestination.AddFunds.route) {
                        launchSingleTop = true
                    }
                },
                onAddCardClick = {
                    navController.navigate(NavigationDestination.AddCard.route) {
                        launchSingleTop = true
                    }
                },
                onLogoutClick = {
                    // Wylogowanie obsługiwane jest przez MainScreen->ProfileScreen->ViewModel
                    // Tutaj obsługujemy tylko nawigację po wylogowaniu
                    navController.navigate(NavigationDestination.Login.route) {
                        popUpTo(NavigationDestination.Main.route) { inclusive = true }
                    }
                }
            )
        }

        // Add Funds with pre-selected venue
        composable(
            route = "${NavigationDestination.AddFunds.route}/{premisesId}",
            arguments = listOf(navArgument("premisesId") { type = NavType.IntType })
        ) { backStackEntry ->
            val premisesId = backStackEntry.savedStateHandle.get<Int>("premisesId") ?: 0
            val balanceViewModel = koinViewModel<BalanceViewModel>()
            val uiState by balanceViewModel.uiState.collectAsState()
            val premises = uiState.balances.find { it.premisesId == premisesId }

            LaunchedEffect(uiState.isTopUpSuccess) {
                if (uiState.isTopUpSuccess) {
                    balanceViewModel.onTopUpSuccessConsumed()
                    navController.popBackStack()
                }
            }

            AddFundsScreen(
                availablePaymentMethods = uiState.paymentMethods,
                onBackClick = { navController.popBackStack() },
                onAddFunds = { paymentMethodId, balance, blikCode ->
                    balanceViewModel.onAddFunds(premisesId, paymentMethodId, balance, blikCode)
                },
                onCancelTopUp = {
                    balanceViewModel.onCancelTopUp()
                },
                isLoading = uiState.isLoading,
                premisesName = premises?.premisesName
            )
        }

        // Add Card screen
        composable(NavigationDestination.AddCard.route) {
            val cardsViewModel = koinViewModel<CardsViewModel>()
            val uiState by cardsViewModel.uiState.collectAsState()

            AddCardScreen(
                scannedCardId = uiState.scannedCardId,
                isNfcEnabled = uiState.isNfcEnabled,
                onBackClick = { navController.popBackStack() },
                onStartScanning = { cardsViewModel.startNfcListening() },
                onStopScanning = { cardsViewModel.stopNfcListening() },
                onSaveCard = { cardId, description ->
                    cardsViewModel.onSaveCard(cardId, description)
                    navController.popBackStack()
                }
            )
        }
    }
}
