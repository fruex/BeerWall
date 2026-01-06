package org.fruex.beerwall.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.fruex.beerwall.ui.models.UserCard
import org.fruex.beerwall.ui.models.VenueBalance
import org.fruex.beerwall.ui.models.DailyTransactions
import org.fruex.beerwall.ui.models.UserProfile
import org.fruex.beerwall.ui.screens.auth.LoginScreen
import org.fruex.beerwall.ui.screens.auth.RegistrationScreen
import org.fruex.beerwall.ui.screens.balance.AddFundsScreen
import org.fruex.beerwall.ui.screens.cards.AddCardScreen

@Composable
fun BeerWallNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationDestination.Main.route,
    // Data
    balances: List<VenueBalance> = emptyList(),
    cards: List<UserCard> = emptyList(),
    transactionGroups: List<DailyTransactions> = emptyList(),
    userProfile: UserProfile = UserProfile("", "", "", 0, 0),
    paymentMethods: List<org.fruex.beerwall.remote.dto.operators.PaymentMethod> = emptyList(),
    isRefreshing: Boolean = false,
    // Callbacks
    onRegister: (email: String, password: String) -> Unit = { _, _ -> },
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onGoogleSignIn: (onSuccess: () -> Unit) -> Unit = { _ -> },
    onLogout: () -> Unit = {},
    onAddFunds: (paymentMethodId: Int, balance: Double) -> Unit = { _, _ -> },
    onToggleCardStatus: (String) -> Unit = {},
    onDeleteCard: (String) -> Unit = {},
    onSaveCard: (name: String, cardId: String) -> Unit = { _, _ -> },
    onStartNfcScanning: () -> Unit = {},
    onRefreshHistory: () -> Unit = {},
    onRefreshBalance: () -> Unit = {},
    scannedCardId: String? = null,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth screens
        composable(NavigationDestination.Registration.route) {
            RegistrationScreen(
                onRegisterClick = { email, password ->
                    onRegister(email, password)
                    navController.navigate(NavigationDestination.Main.route) {
                        popUpTo(NavigationDestination.Registration.route) { inclusive = true }
                    }
                },
                onGoogleSignInClick = {
                    onGoogleSignIn {
                        navController.navigate(NavigationDestination.Main.route) {
                            popUpTo(NavigationDestination.Registration.route) { inclusive = true }
                        }
                    }
                },
                onLoginClick = {
                    navController.navigate(NavigationDestination.Login.route)
                }
            )
        }

        composable(NavigationDestination.Login.route) {
            LoginScreen(
                onLoginClick = { email, password ->
                    onLogin(email, password)
                    navController.navigate(NavigationDestination.Main.route) {
                        popUpTo(NavigationDestination.Login.route) { inclusive = true }
                    }
                },
                onGoogleSignInClick = {
                    onGoogleSignIn {
                        navController.navigate(NavigationDestination.Main.route) {
                            popUpTo(NavigationDestination.Login.route) { inclusive = true }
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(NavigationDestination.Registration.route)
                }
            )
        }

        // Main screen with bottom navigation
        composable(NavigationDestination.Main.route) {
            MainScreen(
                balances = balances,
                onAddFundsClick = { venueName ->
                    navController.navigate("${NavigationDestination.AddFunds.route}/$venueName")
                },
                onAddLocationClick = {
                    navController.navigate(NavigationDestination.AddFunds.route)
                },
                onRefreshBalance = onRefreshBalance,
                cards = cards,
                onAddCardClick = {
                    navController.navigate(NavigationDestination.AddCard.route)
                },
                onToggleCardStatus = onToggleCardStatus,
                onDeleteCard = onDeleteCard,
                transactionGroups = transactionGroups,
                onRefreshHistory = onRefreshHistory,
                isRefreshing = isRefreshing,
                userProfile = userProfile,
                onLogoutClick = {
                    onLogout()
                    navController.navigate(NavigationDestination.Login.route) {
                        popUpTo(NavigationDestination.Main.route) { inclusive = true }
                    }
                }
            )
        }

        // Add Funds screen
        composable(NavigationDestination.AddFunds.route) {
            AddFundsScreen(
                availablePaymentMethods = paymentMethods,
                onBackClick = { navController.popBackStack() },
                onAddFunds = { paymentMethodId, balance ->
                    onAddFunds(paymentMethodId, balance)
                    navController.popBackStack()
                }
            )
        }

        // Add Funds with pre-selected venue
        composable("${NavigationDestination.AddFunds.route}/{venueName}") { backStackEntry ->
            AddFundsScreen(
                availablePaymentMethods = paymentMethods,
                onBackClick = { navController.popBackStack() },
                onAddFunds = { paymentMethodId, balance ->
                    onAddFunds(paymentMethodId, balance)
                    navController.popBackStack()
                }
            )
        }

        // Add Card screen
        composable(NavigationDestination.AddCard.route) {
            AddCardScreen(
                scannedCardId = scannedCardId,
                onBackClick = { navController.popBackStack() },
                onStartScanning = onStartNfcScanning,
                onCardNameChanged = {},
                onSaveCard = { name, cardId ->
                    onSaveCard(name, cardId)
                    navController.popBackStack()
                }
            )
        }
    }
}
