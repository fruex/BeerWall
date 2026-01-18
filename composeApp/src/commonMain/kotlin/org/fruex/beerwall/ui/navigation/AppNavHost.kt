package org.fruex.beerwall.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.fruex.beerwall.di.AppContainer
import org.fruex.beerwall.ui.screens.auth.AuthMode
import org.fruex.beerwall.ui.screens.auth.AuthScreen
import org.fruex.beerwall.ui.screens.balance.AddFundsScreen
import org.fruex.beerwall.ui.screens.cards.AddCardScreen
import org.fruex.beerwall.ui.screens.profile.AboutScreen
import org.fruex.beerwall.ui.screens.profile.ChangePasswordScreen
import org.fruex.beerwall.ui.screens.profile.SupportScreen

/**
 * Główny komponent nawigacyjny aplikacji (NavHost).
 *
 * Definiuje graf nawigacji i obsługuje przejścia między ekranami.
 *
 * @param modifier Modyfikator układu.
 * @param appContainer Kontener zależności.
 * @param navController Kontroler nawigacji.
 * @param startDestination Punkt startowy nawigacji.
 * @param scannedCardId Zeskanowane ID karty (stan) - TODO: Przenieść do CardsViewModel.
 * @param isNfcEnabled Flaga dostępności NFC (stan).
 * @param onStartNfcScanningClick Callback startu skanowania NFC.
 */
@Composable
fun AppNavHost(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationDestination.Main.route,
    scannedCardId: String? = null,
    isNfcEnabled: Boolean = true,
    onStartNfcScanningClick: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth screens
        composable(NavigationDestination.Registration.route) {
            val authViewModel = viewModel { appContainer.createAuthViewModel() }
            val uiState by authViewModel.uiState.collectAsState()

            AuthScreen(
                mode = AuthMode.REGISTER,
                onAuthClick = { email, password ->
                    authViewModel.handleRegister(email, password)
                },
                onGoogleSignInClick = { /* TODO: Google SignIn flow from Screen or hoist provider */ },
                onToggleModeClick = {
                    navController.navigate(NavigationDestination.Login.route) {
                        popUpTo(NavigationDestination.Registration.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                isLoading = uiState.isLoading
            )
        }

        composable(NavigationDestination.Login.route) {
            val authViewModel = viewModel { appContainer.createAuthViewModel() }
            val uiState by authViewModel.uiState.collectAsState()

            AuthScreen(
                mode = AuthMode.LOGIN,
                onAuthClick = { email, password ->
                    authViewModel.handleEmailPasswordSignIn(email, password)
                },
                onGoogleSignInClick = { /* TODO */ },
                onToggleModeClick = {
                    navController.navigate(NavigationDestination.Registration.route) {
                        popUpTo(NavigationDestination.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = { email ->
                    authViewModel.handleForgotPassword(email)
                },
                isLoading = uiState.isLoading
            )
        }

        // Main screen with bottom navigation
        composable(NavigationDestination.Main.route) {
            MainScreen(
                appContainer = appContainer,
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
                },
                onChangePasswordClick = {
                    navController.navigate(NavigationDestination.ChangePassword.route) {
                        launchSingleTop = true
                    }
                },
                onSupportClick = {
                    navController.navigate(NavigationDestination.Support.route) {
                        launchSingleTop = true
                    }
                },
                onAboutClick = {
                    navController.navigate(NavigationDestination.About.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Add Funds screen
        composable(NavigationDestination.AddFunds.route) {
            val balanceViewModel = viewModel { appContainer.createBalanceViewModel() }
            val uiState by balanceViewModel.uiState.collectAsState()

            AddFundsScreen(
                availablePaymentMethods = uiState.paymentMethods,
                onBackClick = { navController.popBackStack() },
                onAddFunds = { paymentMethodId, balance ->
                    // Domyślny lokal
                    val venueId = uiState.balances.firstOrNull()?.premisesId ?: 0
                    balanceViewModel.onAddFunds(venueId, paymentMethodId, balance)
                    navController.popBackStack()
                }
            )
        }

        // Add Funds with pre-selected venue
        composable(
            route = "${NavigationDestination.AddFunds.route}/{venueId}",
            arguments = listOf(navArgument("venueId") { type = NavType.IntType })
        ) { backStackEntry ->
            val venueId = backStackEntry.savedStateHandle.get<Int>("venueId") ?: 0
            val balanceViewModel = viewModel { appContainer.createBalanceViewModel() }
            val uiState by balanceViewModel.uiState.collectAsState()
            val venue = uiState.balances.find { it.premisesId == venueId }

            AddFundsScreen(
                availablePaymentMethods = uiState.paymentMethods,
                onBackClick = { navController.popBackStack() },
                onAddFunds = { paymentMethodId, balance ->
                    balanceViewModel.onAddFunds(venueId, paymentMethodId, balance)
                    navController.popBackStack()
                },
                premisesName = venue?.premisesName
            )
        }

        // Add Card screen
        composable(NavigationDestination.AddCard.route) {
            val cardsViewModel = viewModel { appContainer.createCardsViewModel() }

            AddCardScreen(
                scannedCardId = scannedCardId,
                isNfcEnabled = isNfcEnabled,
                onBackClick = { navController.popBackStack() },
                onStartScanning = onStartNfcScanningClick,
                onCardNameChanged = {},
                onSaveCard = { name, cardId ->
                    cardsViewModel.onSaveCard(name, cardId)
                    navController.popBackStack()
                }
            )
        }

        // Profile sub-screens
        composable(NavigationDestination.ChangePassword.route) {
            val authViewModel = viewModel { appContainer.createAuthViewModel() }

            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() },
                onResetPassword = { email, resetCode, newPassword ->
                    authViewModel.handleResetPassword(email, resetCode, newPassword)
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationDestination.Support.route) {
            val profileViewModel = viewModel { appContainer.createProfileViewModel() }

            SupportScreen(
                onBackClick = { navController.popBackStack() },
                onSendMessage = { message ->
                    profileViewModel.onSendMessage(message)
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationDestination.About.route) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
