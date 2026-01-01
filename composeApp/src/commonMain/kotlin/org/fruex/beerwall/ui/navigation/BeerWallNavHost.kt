package org.fruex.beerwall.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.fruex.beerwall.ui.models.CardItem
import org.fruex.beerwall.ui.models.LocationBalance
import org.fruex.beerwall.ui.models.TransactionGroup
import org.fruex.beerwall.ui.models.UserProfile
import org.fruex.beerwall.ui.screens.auth.RegistrationScreen

@Composable
fun BeerWallNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationDestination.Main.route,
    // Data
    balances: List<LocationBalance> = emptyList(),
    cards: List<CardItem> = emptyList(),
    transactionGroups: List<TransactionGroup> = emptyList(),
    userProfile: UserProfile = UserProfile("", "", "", 0, 0),
    // Callbacks
    onRegister: (email: String, password: String) -> Unit = { _, _ -> },
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onGoogleSignIn: () -> Unit = {},
    onLogout: () -> Unit = {},
    onAddFunds: (location: String, amount: Double) -> Unit = { _, _ -> },
    onToggleCardStatus: (String) -> Unit = {},
    onDeleteCard: (String) -> Unit = {},
    onSaveCard: (name: String, cardId: String) -> Unit = { _, _ -> },
    onStartNfcScanning: () -> Unit = {},
    scannedCardId: String? = null,
    isNfcScanning: Boolean = false,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
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
                    onGoogleSignIn()
                    navController.navigate(NavigationDestination.Main.route) {
                        popUpTo(NavigationDestination.Registration.route) { inclusive = true }
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
                    onGoogleSignIn()
                    navController.navigate(NavigationDestination.Main.route) {
                        popUpTo(NavigationDestination.Login.route) { inclusive = true }
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
                onAddFundsClick = { location ->
                    navController.navigate("${NavigationDestination.AddFunds.route}/$location")
                },
                onAddLocationClick = {
                    navController.navigate(NavigationDestination.AddFunds.route)
                },
                cards = cards,
                onAddCardClick = {
                    navController.navigate(NavigationDestination.AddCard.route)
                },
                onToggleCardStatus = onToggleCardStatus,
                onDeleteCard = onDeleteCard,
                transactionGroups = transactionGroups,
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
            val availableLocations = balances.map { it.locationName }
            AddFundsScreen(
                availableLocations = availableLocations,
                selectedLocation = availableLocations.firstOrNull(),
                onLocationSelected = {},
                onBackClick = { navController.popBackStack() },
                onAddFunds = { location, amount ->
                    onAddFunds(location, amount)
                    navController.popBackStack()
                }
            )
        }

        // Add Funds with pre-selected location
        composable("${NavigationDestination.AddFunds.route}/{location}") { backStackEntry ->
            val location = backStackEntry.arguments?.getString("location")
            val availableLocations = balances.map { it.locationName }
            AddFundsScreen(
                availableLocations = availableLocations,
                selectedLocation = location,
                onLocationSelected = {},
                onBackClick = { navController.popBackStack() },
                onAddFunds = { loc, amount ->
                    onAddFunds(loc, amount)
                    navController.popBackStack()
                }
            )
        }

        // Add Card screen
        composable(NavigationDestination.AddCard.route) {
            AddCardScreen(
                isScanning = isNfcScanning,
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
