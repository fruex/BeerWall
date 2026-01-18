package org.fruex.beerwall.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.fruex.beerwall.auth.rememberGoogleAuthProvider
import org.fruex.beerwall.presentation.viewmodel.AuthViewModel
import org.fruex.beerwall.presentation.viewmodel.BalanceViewModel
import org.fruex.beerwall.presentation.viewmodel.CardsViewModel
import org.fruex.beerwall.presentation.viewmodel.HistoryViewModel
import org.fruex.beerwall.presentation.viewmodel.ProfileViewModel
import org.fruex.beerwall.ui.screens.balance.BalanceScreen
import org.fruex.beerwall.ui.screens.cards.CardsScreen
import org.fruex.beerwall.ui.screens.history.HistoryScreen
import org.fruex.beerwall.ui.screens.profile.ProfileScreen
import org.fruex.beerwall.ui.theme.CardBackground
import org.fruex.beerwall.ui.theme.GoldPrimary
import org.koin.compose.viewmodel.koinViewModel

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Balance : BottomNavItem(
        route = "balance",
        label = "Saldo",
        selectedIcon = Icons.Filled.Wallet,
        unselectedIcon = Icons.Outlined.Wallet
    )

    data object Cards : BottomNavItem(
        route = "cards",
        label = "Karty",
        selectedIcon = Icons.Filled.CreditCard,
        unselectedIcon = Icons.Outlined.CreditCard
    )

    data object History : BottomNavItem(
        route = "history",
        label = "Historia",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )

    data object Profile : BottomNavItem(
        route = "profile",
        label = "Profil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

/**
 * Główny ekran aplikacji zawierający dolny pasek nawigacyjny.
 *
 * @param onAddFundsClick Callback do ekranu doładowania.
 * @param onAddLocationClick Callback do dodawania lokalizacji.
 * @param onAddCardClick Callback do dodawania karty.
 * @param onLogoutClick Callback wylogowania.
 * @param onChangePasswordClick Callback zmiany hasła.
 * @param onSupportClick Callback pomocy.
 * @param onAboutClick Callback "O aplikacji".
 */
@Composable
fun MainScreen(
    onAddFundsClick: (premisesId: Int) -> Unit = {},
    onAddLocationClick: () -> Unit = {},
    onAddCardClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavItem.Balance.route) }

    // ViewModels for each tab
    val balanceViewModel = koinViewModel<BalanceViewModel>()
    val cardsViewModel = koinViewModel<CardsViewModel>()
    val historyViewModel = koinViewModel<HistoryViewModel>()
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val authViewModel = koinViewModel<AuthViewModel>()

    // Collect states
    val balanceState by balanceViewModel.uiState.collectAsState()
    val cardsState by cardsViewModel.uiState.collectAsState()
    val historyState by historyViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState() // For user profile

    val googleAuthProvider = rememberGoogleAuthProvider()

    val items = remember {
        listOf(
            BottomNavItem.Balance,
            BottomNavItem.Cards,
            BottomNavItem.History,
            BottomNavItem.Profile
        )
    }

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            BottomNavItem.Balance.route -> balanceViewModel.refreshBalance()
            BottomNavItem.History.route -> historyViewModel.refreshHistory()
            BottomNavItem.Cards.route -> cardsViewModel.refreshCards()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = CardBackground,
                contentColor = GoldPrimary
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == item.route) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = selectedTab == item.route,
                        onClick = { selectedTab = item.route },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GoldPrimary,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary.copy(alpha = 0.2f),
                            unselectedIconColor = GoldPrimary.copy(alpha = 0.5f),
                            unselectedTextColor = GoldPrimary.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                BottomNavItem.Balance.route -> {
                    BalanceScreen(
                        balances = balanceState.balances,
                        isRefreshing = balanceState.isRefreshing,
                        onRefresh = { balanceViewModel.refreshBalance() },
                        onAddFundsClick = onAddFundsClick,
                        onAddLocationClick = onAddLocationClick
                    )
                }
                BottomNavItem.Cards.route -> {
                    CardsScreen(
                        cards = cardsState.cards,
                        onAddCardClick = onAddCardClick,
                        onToggleCardStatus = { cardsViewModel.onToggleCardStatus(it) },
                        onDeleteCard = { cardsViewModel.onDeleteCard(it) }
                    )
                }
                BottomNavItem.History.route -> {
                    HistoryScreen(
                        transactionGroups = historyState.transactionGroups,
                        isRefreshing = historyState.isRefreshing,
                        onRefresh = { historyViewModel.refreshHistory() }
                    )
                }
                BottomNavItem.Profile.route -> {
                    ProfileScreen(
                        userProfile = authState.userProfile,
                        onLogoutClick = {
                            authViewModel.handleLogout(googleAuthProvider)
                            onLogoutClick()
                        },
                        onChangePasswordClick = onChangePasswordClick,
                        onSupportClick = onSupportClick,
                        onAboutClick = onAboutClick
                    )
                }
            }
        }
    }
}
