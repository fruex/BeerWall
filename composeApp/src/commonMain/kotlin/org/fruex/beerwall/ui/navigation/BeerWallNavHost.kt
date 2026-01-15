package org.fruex.beerwall.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.fruex.beerwall.ui.models.DailyTransactions
import org.fruex.beerwall.ui.models.UserCard
import org.fruex.beerwall.ui.models.UserProfile
import org.fruex.beerwall.ui.models.VenueBalance
import org.fruex.beerwall.ui.screens.auth.LoginScreen
import org.fruex.beerwall.ui.screens.auth.RegistrationScreen
import org.fruex.beerwall.ui.screens.balance.AddFundsScreen
import org.fruex.beerwall.ui.screens.cards.AddCardScreen
import org.fruex.beerwall.ui.screens.profile.AboutScreen
import org.fruex.beerwall.ui.screens.profile.ChangePasswordScreen
import org.fruex.beerwall.ui.screens.profile.SupportScreen

@Composable
fun BeerWallNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationDestination.Main.route,
    // Data
    balances: List<VenueBalance> = emptyList(),
    cards: List<UserCard> = emptyList(),
    transactionGroups: List<DailyTransactions> = emptyList(),
    userProfile: UserProfile = UserProfile("", "", ""),
    paymentMethods: List<org.fruex.beerwall.remote.dto.operators.PaymentMethod> = emptyList(),
    isRefreshing: Boolean = false,
    // Callbacks
    onRegisterWithEmail: (email: String, password: String) -> Unit = { _, _ -> },
    onLoginWithEmail: (email: String, password: String) -> Unit = { _, _ -> },
    onLoginWithGoogle: () -> Unit = {},
    onLogout: () -> Unit = {},
    onAddFunds: (premisesId: Int, paymentMethodId: Int, balance: Double) -> Unit = { _, _, _ -> },
    onToggleCardStatus: (String) -> Unit = {},
    onDeleteCard: (String) -> Unit = {},
    onSaveCard: (name: String, cardId: String) -> Unit = { _, _ -> },
    onStartNfcScanning: () -> Unit = {},
    onRefreshHistory: () -> Unit = {},
    onRefreshBalance: () -> Unit = {},
    onForgotPassword: (email: String) -> Unit = {},
    onResetPassword: (email: String) -> Unit = {},
    onSendMessage: (message: String) -> Unit = {},
    scannedCardId: String? = null,
    isNfcEnabled: Boolean = true,
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
                    onRegisterWithEmail(email, password)
                },
                onGoogleSignInClick = onLoginWithGoogle,
                onLoginClick = {
                    navController.navigate(NavigationDestination.Login.route)
                }
            )
        }

        composable(NavigationDestination.Login.route) {
            LoginScreen(
                onLoginClick = { email, password ->
                    onLoginWithEmail(email, password)
                },
                onGoogleSignInClick = onLoginWithGoogle,
                onRegisterClick = {
                    navController.navigate(NavigationDestination.Registration.route)
                },
                isLoading = isRefreshing
            )
        }

        // Main screen with bottom navigation
        composable(NavigationDestination.Main.route) {
            MainScreen(
                balances = balances,
                onAddFundsClick = { premisesId ->
                    navController.navigate("${NavigationDestination.AddFunds.route}/$premisesId")
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
                },
                onChangePasswordClick = {
                    navController.navigate(NavigationDestination.ChangePassword.route)
                },
                onSupportClick = {
                    navController.navigate(NavigationDestination.Support.route)
                },
                onAboutClick = {
                    navController.navigate(NavigationDestination.About.route)
                }
            )
        }

        // Add Funds screen
        composable(NavigationDestination.AddFunds.route) {
            AddFundsScreen(
                availablePaymentMethods = paymentMethods,
                onBackClick = { navController.popBackStack() },
                onAddFunds = { paymentMethodId, balance ->
                    // Domyślny lokal jeśli nie został wybrany
                    onAddFunds(balances.firstOrNull()?.premisesId ?: 0, paymentMethodId, balance)
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
            val venue = balances.find { it.premisesId == venueId }
            AddFundsScreen(
                availablePaymentMethods = paymentMethods,
                onBackClick = { navController.popBackStack() },
                onAddFunds = { paymentMethodId, balance ->
                    onAddFunds(venueId, paymentMethodId, balance)
                    navController.popBackStack()
                },
                premisesName = venue?.premisesName
            )
        }

        // Add Card screen
        composable(NavigationDestination.AddCard.route) {
            AddCardScreen(
                scannedCardId = scannedCardId,
                isNfcEnabled = isNfcEnabled,
                onBackClick = { navController.popBackStack() },
                onStartScanning = onStartNfcScanning,
                onCardNameChanged = {},
                onSaveCard = { name, cardId ->
                    onSaveCard(name, cardId)
                    navController.popBackStack()
                }
            )
        }

        // Profile sub-screens
        composable(NavigationDestination.ChangePassword.route) {
            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() },
                onForgotPassword = { email ->
                    onResetPassword(email)
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationDestination.Support.route) {
            SupportScreen(
                onBackClick = { navController.popBackStack() },
                onSendMessage = { message ->
                    onSendMessage(message)
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
