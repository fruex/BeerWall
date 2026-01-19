package com.fruex.beerwall.ui.navigation

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
import com.fruex.beerwall.auth.rememberGoogleAuthProvider
import com.fruex.beerwall.presentation.viewmodel.AuthViewModel
import com.fruex.beerwall.presentation.viewmodel.BalanceUiState
import com.fruex.beerwall.presentation.viewmodel.BalanceViewModel
import com.fruex.beerwall.presentation.viewmodel.CardsUiState
import com.fruex.beerwall.presentation.viewmodel.CardsViewModel
import com.fruex.beerwall.presentation.viewmodel.HistoryUiState
import com.fruex.beerwall.presentation.viewmodel.HistoryViewModel
import com.fruex.beerwall.presentation.viewmodel.ProfileViewModel
import com.fruex.beerwall.ui.models.UserCard
import com.fruex.beerwall.ui.models.UserProfile
import com.fruex.beerwall.ui.models.VenueBalance
import com.fruex.beerwall.ui.screens.balance.BalanceScreen
import com.fruex.beerwall.ui.screens.cards.CardsScreen
import com.fruex.beerwall.ui.screens.history.HistoryScreen
import com.fruex.beerwall.ui.screens.profile.ProfileScreen
import com.fruex.beerwall.ui.theme.BeerWallTheme
import com.fruex.beerwall.ui.theme.CardBackground
import com.fruex.beerwall.ui.theme.GoldPrimary
import org.jetbrains.compose.ui.tooling.preview.Preview
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
    val profileViewModel = koinViewModel<ProfileViewModel>() // Keep explicitly if needed later, though unused now
    val authViewModel = koinViewModel<AuthViewModel>()

    // Collect states
    val balanceState by balanceViewModel.uiState.collectAsState()
    val cardsState by cardsViewModel.uiState.collectAsState()
    val historyState by historyViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState() // For user profile

    val googleAuthProvider = rememberGoogleAuthProvider()

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            BottomNavItem.Balance.route -> balanceViewModel.refreshBalance()
            BottomNavItem.History.route -> historyViewModel.refreshHistory()
            BottomNavItem.Cards.route -> cardsViewModel.refreshCards()
        }
    }

    MainScreenContent(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        balanceState = balanceState,
        cardsState = cardsState,
        historyState = historyState,
        userProfile = authState.userProfile,
        onAddFundsClick = onAddFundsClick,
        onAddLocationClick = onAddLocationClick,
        onAddCardClick = onAddCardClick,
        onLogoutClick = {
            authViewModel.handleLogout(googleAuthProvider)
            onLogoutClick()
        },
        onChangePasswordClick = onChangePasswordClick,
        onSupportClick = onSupportClick,
        onAboutClick = onAboutClick,
        onRefreshBalance = { balanceViewModel.refreshBalance() },
        onRefreshHistory = { historyViewModel.refreshHistory() },
        onToggleCardStatus = { cardsViewModel.onToggleCardStatus(it) },
        onDeleteCard = { cardsViewModel.onDeleteCard(it) }
    )
}

@Composable
fun MainScreenContent(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    balanceState: BalanceUiState,
    cardsState: CardsUiState,
    historyState: HistoryUiState,
    userProfile: UserProfile,
    onAddFundsClick: (Int) -> Unit,
    onAddLocationClick: () -> Unit,
    onAddCardClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onSupportClick: () -> Unit,
    onAboutClick: () -> Unit,
    onRefreshBalance: () -> Unit,
    onRefreshHistory: () -> Unit,
    onToggleCardStatus: (String) -> Unit,
    onDeleteCard: (String) -> Unit
) {
    val items = remember {
        listOf(
            BottomNavItem.Balance,
            BottomNavItem.Cards,
            BottomNavItem.History,
            BottomNavItem.Profile
        )
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
                        onClick = { onTabSelected(item.route) },
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
                        onRefresh = onRefreshBalance,
                        onAddFundsClick = onAddFundsClick,
                        onAddLocationClick = onAddLocationClick
                    )
                }
                BottomNavItem.Cards.route -> {
                    CardsScreen(
                        cards = cardsState.cards,
                        onAddCardClick = onAddCardClick,
                        onToggleCardStatus = onToggleCardStatus,
                        onDeleteCard = onDeleteCard
                    )
                }
                BottomNavItem.History.route -> {
                    HistoryScreen(
                        transactionGroups = historyState.transactionGroups,
                        isRefreshing = historyState.isRefreshing,
                        onRefresh = onRefreshHistory
                    )
                }
                BottomNavItem.Profile.route -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        onLogoutClick = onLogoutClick,
                        onChangePasswordClick = onChangePasswordClick,
                        onSupportClick = onSupportClick,
                        onAboutClick = onAboutClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MainScreenPreviewTemplate(selectedTab: String) {
    BeerWallTheme {
        MainScreenContent(
            selectedTab = selectedTab,
            onTabSelected = {},
            balanceState = BalanceUiState(
                balances = listOf(
                    VenueBalance(
                        premisesId = 1,
                        premisesName = "Pub Centrum",
                        balance = 45.50,
                        loyaltyPoints = 120
                    ),
                    VenueBalance(
                        premisesId = 2,
                        premisesName = "Bar Rynek",
                        balance = 12.00,
                        loyaltyPoints = 50
                    )
                )
            ),
            cardsState = CardsUiState(
                cards = listOf(
                    UserCard(
                        id = "123",
                        name = "Moja karta",
                        isActive = true,
                        isPhysical = true
                    ),
                    UserCard(
                        id = "456",
                        name = "Karta wirtualna",
                        isActive = false,
                        isPhysical = false
                    )
                )
            ),
            historyState = HistoryUiState(),
            userProfile = UserProfile(
                name = "Jan Kowalski",
                initials = "JK"
            ),
            onAddFundsClick = {},
            onAddLocationClick = {},
            onAddCardClick = {},
            onLogoutClick = {},
            onChangePasswordClick = {},
            onSupportClick = {},
            onAboutClick = {},
            onRefreshBalance = {},
            onRefreshHistory = {},
            onToggleCardStatus = {},
            onDeleteCard = {}
        )
    }
}

@Preview
@Composable
fun MainScreenBalancePreview() {
    MainScreenPreviewTemplate(BottomNavItem.Balance.route)
}

@Preview
@Composable
fun MainScreenCardsPreview() {
    MainScreenPreviewTemplate(BottomNavItem.Cards.route)
}

@Preview
@Composable
fun MainScreenHistoryPreview() {
    MainScreenPreviewTemplate(BottomNavItem.History.route)
}

@Preview
@Composable
fun MainScreenProfilePreview() {
    MainScreenPreviewTemplate(BottomNavItem.Profile.route)
}
