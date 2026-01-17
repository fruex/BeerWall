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
 * @param navController Kontroler nawigacji.
 * @param startDestination Punkt startowy nawigacji.
 * @param balances Lista sald (stan).
 * @param cards Lista kart (stan).
 * @param transactionGroups Historia transakcji (stan).
 * @param userProfile Profil użytkownika (stan).
 * @param paymentMethods Metody płatności (stan).
 * @param isRefreshing Flaga odświeżania (stan).
 * @param onRegisterWithEmail Callback rejestracji.
 * @param onLoginWithEmail Callback logowania email/hasło.
 * @param onLoginWithGoogleClick Callback logowania Google.
 * @param onLogoutClick Callback wylogowania.
 * @param onAddFundsClick Callback doładowania konta.
 * @param onToggleCardStatusClick Callback zmiany statusu karty.
 * @param onDeleteCardClick Callback usuwania karty.
 * @param onSaveCardClick Callback zapisywania karty.
 * @param onStartNfcScanningClick Callback startu skanowania NFC.
 * @param onRefreshHistoryClick Callback odświeżania historii.
 * @param onRefreshBalanceClick Callback odświeżania salda.
 * @param onForgotPassword Callback przypomnienia hasła.
 * @param onResetPassword Callback resetu hasła.
 * @param scannedCardId Zeskanowane ID karty (stan).
 * @param isNfcEnabled Flaga dostępności NFC (stan).
 */
@Composable
fun AppNavHost(
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
    onLoginWithGoogleClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAddFundsClick: (premisesId: Int, paymentMethodId: Int, balance: Double) -> Unit = { _, _, _ -> },
    onToggleCardStatusClick: (String) -> Unit = {},
    onDeleteCardClick: (String) -> Unit = {},
    onSaveCardClick: (name: String, cardId: String) -> Unit = { _, _ -> },
    onStartNfcScanningClick: () -> Unit = {},
    onRefreshHistoryClick: () -> Unit = {},
    onRefreshBalanceClick: () -> Unit = {},
    onForgotPassword: (email: String) -> Unit = {},
    onResetPassword: (String, String, String) -> Unit = { _, _, _ -> },
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
            AuthScreen(
                mode = AuthMode.REGISTER,
                onAuthClick = { email, password ->
                    onRegisterWithEmail(email, password)
                },
                onGoogleSignInClick = onLoginWithGoogleClick,
                onToggleModeClick = {
                    navController.navigate(NavigationDestination.Login.route) {
                        popUpTo(NavigationDestination.Registration.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                isLoading = isRefreshing
            )
        }

        composable(NavigationDestination.Login.route) {
            AuthScreen(
                mode = AuthMode.LOGIN,
                onAuthClick = { email, password ->
                    onLoginWithEmail(email, password)
                },
                onGoogleSignInClick = onLoginWithGoogleClick,
                onToggleModeClick = {
                    navController.navigate(NavigationDestination.Registration.route) {
                        popUpTo(NavigationDestination.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = { email ->
                    onForgotPassword(email)
                },
                isLoading = isRefreshing
            )
        }

        // Main screen with bottom navigation
        composable(NavigationDestination.Main.route) {
            MainScreen(
                balances = balances,
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
                onRefreshBalanceClick = onRefreshBalanceClick,
                cards = cards,
                onAddCardClick = {
                    navController.navigate(NavigationDestination.AddCard.route) {
                        launchSingleTop = true
                    }
                },
                onToggleCardStatusClick = onToggleCardStatusClick,
                onDeleteCardClick = onDeleteCardClick,
                transactionGroups = transactionGroups,
                onRefreshHistoryClick = onRefreshHistoryClick,
                isRefreshing = isRefreshing,
                userProfile = userProfile,
                onLogoutClick = {
                    onLogoutClick()
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
            AddFundsScreen(
                availablePaymentMethods = paymentMethods,
                onBackClick = { navController.popBackStack() },
                onAddFunds = { paymentMethodId, balance ->
                    // Domyślny lokal jeśli nie został wybrany
                    onAddFundsClick(balances.firstOrNull()?.premisesId ?: 0, paymentMethodId, balance)
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
                    onAddFundsClick(venueId, paymentMethodId, balance)
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
                onStartScanning = onStartNfcScanningClick,
                onCardNameChanged = {},
                onSaveCard = { name, cardId ->
                    onSaveCardClick(name, cardId)
                    navController.popBackStack()
                }
            )
        }

        // Profile sub-screens
        composable(NavigationDestination.ChangePassword.route) {
            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() },
                onResetPassword = { email, resetCode, newPassword ->
                    onResetPassword(email, resetCode, newPassword)
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
