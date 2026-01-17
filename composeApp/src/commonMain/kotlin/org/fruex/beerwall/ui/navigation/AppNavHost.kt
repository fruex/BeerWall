package org.fruex.beerwall.ui.navigation

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
import org.fruex.beerwall.auth.rememberGoogleAuthProvider
import org.fruex.beerwall.presentation.viewmodel.AuthViewModel
import org.fruex.beerwall.presentation.viewmodel.BalanceViewModel
import org.fruex.beerwall.presentation.viewmodel.CardsViewModel
import org.fruex.beerwall.presentation.viewmodel.HistoryViewModel
import org.fruex.beerwall.presentation.viewmodel.ProfileViewModel
import org.fruex.beerwall.ui.screens.auth.AuthMode
import org.fruex.beerwall.ui.screens.auth.AuthScreen
import org.fruex.beerwall.ui.screens.balance.AddFundsScreen
import org.fruex.beerwall.ui.screens.cards.AddCardScreen
import org.fruex.beerwall.ui.screens.profile.AboutScreen
import org.fruex.beerwall.ui.screens.profile.ChangePasswordScreen
import org.fruex.beerwall.ui.screens.profile.SupportScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationDestination.Login.route,
    scannedCardId: String? = null,
    isNfcEnabled: Boolean = true,
    onStartNfcScanningClick: () -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth screens
        composable(NavigationDestination.Registration.route) {
            val authViewModel = koinViewModel<AuthViewModel>()
            val uiState by authViewModel.uiState.collectAsState()
            val googleAuthProvider = rememberGoogleAuthProvider()

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
                isLoading = uiState.isLoading
            )
        }

        composable(NavigationDestination.Login.route) {
            val authViewModel = koinViewModel<AuthViewModel>()
            val uiState by authViewModel.uiState.collectAsState()
            val googleAuthProvider = rememberGoogleAuthProvider()

            LaunchedEffect(uiState.isLoggedIn) {
                if (uiState.isLoggedIn) {
                    navController.navigate(NavigationDestination.Main.route) {
                        popUpTo(NavigationDestination.Login.route) { inclusive = true }
                    }
                }
            }

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
                isLoading = uiState.isLoading
            )
        }

        // Main screen with bottom navigation
        composable(NavigationDestination.Main.route) {
            val balanceViewModel = koinViewModel<BalanceViewModel>()
            val cardsViewModel = koinViewModel<CardsViewModel>()
            val historyViewModel = koinViewModel<HistoryViewModel>()
            val authViewModel = koinViewModel<AuthViewModel>()

            val balanceState by balanceViewModel.uiState.collectAsState()
            val cardsState by cardsViewModel.uiState.collectAsState()
            val historyState by historyViewModel.uiState.collectAsState()
            val authState by authViewModel.uiState.collectAsState()

            val googleAuthProvider = rememberGoogleAuthProvider()

            LaunchedEffect(Unit) {
                balanceViewModel.refreshBalance()
                cardsViewModel.refreshCards()
                historyViewModel.refreshHistory()
            }

            LaunchedEffect(authState.isLoggedIn) {
                if (!authState.isLoggedIn) {
                    navController.navigate(NavigationDestination.Login.route) {
                         popUpTo(NavigationDestination.Main.route) { inclusive = true }
                    }
                }
            }

            MainScreen(
                balances = balanceState.balances,
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
                onRefreshBalanceClick = { balanceViewModel.refreshBalance() },
                cards = cardsState.cards,
                onAddCardClick = {
                    navController.navigate(NavigationDestination.AddCard.route) {
                        launchSingleTop = true
                    }
                },
                onToggleCardStatusClick = { cardsViewModel.onToggleCardStatus(it) },
                onDeleteCardClick = { cardsViewModel.onDeleteCard(it) },
                transactionGroups = historyState.transactionGroups,
                onRefreshHistoryClick = { historyViewModel.refreshHistory() },
                isRefreshing = balanceState.isRefreshing || cardsState.isRefreshing || historyState.isRefreshing,
                userProfile = authState.userProfile,
                onLogoutClick = {
                    authViewModel.handleLogout(googleAuthProvider)
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
            val balanceViewModel = koinViewModel<BalanceViewModel>()
            val balanceState by balanceViewModel.uiState.collectAsState()

            AddFundsScreen(
                availablePaymentMethods = balanceState.paymentMethods,
                onBackClick = { navController.popBackStack() },
                onAddFunds = { paymentMethodId, balance ->
                    // Domyślny lokal jeśli nie został wybrany
                    balanceViewModel.onAddFunds(balanceState.balances.firstOrNull()?.premisesId ?: 0, paymentMethodId, balance)
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

            val balanceViewModel = koinViewModel<BalanceViewModel>()
            val balanceState by balanceViewModel.uiState.collectAsState()

            val venue = balanceState.balances.find { it.premisesId == venueId }
            AddFundsScreen(
                availablePaymentMethods = balanceState.paymentMethods,
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
            val cardsViewModel = koinViewModel<CardsViewModel>()

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
            val authViewModel = koinViewModel<AuthViewModel>()

            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() },
                onResetPassword = { email, resetCode, newPassword ->
                    authViewModel.handleResetPassword(email, resetCode, newPassword)
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationDestination.Support.route) {
            val profileViewModel = koinViewModel<ProfileViewModel>()

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
