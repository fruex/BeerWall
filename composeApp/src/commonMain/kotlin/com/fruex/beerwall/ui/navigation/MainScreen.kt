package com.fruex.beerwall.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.auth.rememberGoogleAuthProvider
import com.fruex.beerwall.presentation.viewmodel.*
import com.fruex.beerwall.ui.models.DailyTransactions
import com.fruex.beerwall.ui.models.PremisesBalance
import com.fruex.beerwall.ui.models.UserCard
import com.fruex.beerwall.ui.models.UserProfile
import com.fruex.beerwall.ui.screens.balance.BalanceScreen
import com.fruex.beerwall.ui.screens.cards.CardsScreen
import com.fruex.beerwall.ui.screens.history.HistoryScreen
import com.fruex.beerwall.ui.screens.profile.ProfileScreen
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
    val authState by authViewModel.uiState.collectAsState()
    val tiltAngle by profileViewModel.tiltAngle.collectAsState()

    val googleAuthProvider = rememberGoogleAuthProvider()

    var selectedTab by rememberSaveable { mutableStateOf(BottomNavItem.Balance.route) }

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            BottomNavItem.Balance.route -> balanceViewModel.refreshBalance()
            BottomNavItem.History.route -> historyViewModel.refreshHistory()
            BottomNavItem.Cards.route -> cardsViewModel.refreshCards()
        }
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, selectedTab) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME && selectedTab == BottomNavItem.Balance.route) {
                balanceViewModel.refreshBalance()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    MainScreenContent(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        balances = balanceState.balances,
        isBalanceRefreshing = balanceState.isRefreshing,
        onBalanceRefresh = { balanceViewModel.refreshBalance() },
        cards = cardsState.cards,
        isCardsRefreshing = cardsState.isRefreshing,
        onCardsRefresh = { cardsViewModel.refreshCards() },
        onToggleCardStatus = { cardsViewModel.onToggleCardStatus(it) },
        onDeleteCard = { cardsViewModel.onDeleteCard(it) },
        transactionGroups = historyState.transactionGroups,
        isHistoryRefreshing = historyState.isRefreshing,
        onHistoryRefresh = { historyViewModel.refreshHistory() },
        userProfile = authState.userProfile,
        tiltAngle = tiltAngle,
        onLogoutClick = {
            authViewModel.handleLogout(googleAuthProvider)
            onLogoutClick()
        },
        onAddFundsClick = onAddFundsClick,
        onAddLocationClick = onAddLocationClick,
        onAddCardClick = onAddCardClick,
        onChangePasswordClick = onChangePasswordClick,
        onSupportClick = onSupportClick,
        onAboutClick = onAboutClick
    )
}

@Composable
fun MainScreenContent(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    balances: List<PremisesBalance>,
    isBalanceRefreshing: Boolean,
    onBalanceRefresh: () -> Unit,
    cards: List<UserCard>,
    isCardsRefreshing: Boolean,
    onCardsRefresh: () -> Unit,
    onToggleCardStatus: (String) -> Unit,
    onDeleteCard: (String) -> Unit,
    transactionGroups: List<DailyTransactions>,
    isHistoryRefreshing: Boolean,
    onHistoryRefresh: () -> Unit,
    userProfile: UserProfile?,
    tiltAngle: Float,
    onLogoutClick: () -> Unit,
    onAddFundsClick: (premisesId: Int) -> Unit,
    onAddLocationClick: () -> Unit,
    onAddCardClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onSupportClick: () -> Unit,
    onAboutClick: () -> Unit,
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
                contentColor = GoldPrimary,
                modifier = Modifier.height(64.dp),
                windowInsets = WindowInsets(0, 0, 0, 0)
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
                            indicatorColor = Color.Transparent,
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
                        balances = balances,
                        isRefreshing = isBalanceRefreshing,
                        onRefresh = onBalanceRefresh,
                        onAddFundsClick = onAddFundsClick,
                        onAddLocationClick = onAddLocationClick
                    )
                }
                BottomNavItem.Cards.route -> {
                    CardsScreen(
                        cards = cards,
                        isRefreshing = isCardsRefreshing,
                        onRefresh = onCardsRefresh,
                        onAddCardClick = onAddCardClick,
                        onToggleCardStatus = onToggleCardStatus,
                        onDeleteCard = onDeleteCard
                    )
                }
                BottomNavItem.History.route -> {
                    HistoryScreen(
                        transactionGroups = transactionGroups,
                        isRefreshing = isHistoryRefreshing,
                        onRefresh = onHistoryRefresh
                    )
                }
                BottomNavItem.Profile.route -> {
                    if (userProfile != null) {
                        ProfileScreen(
                            userProfile = userProfile,
                            tiltAngle = tiltAngle,
                            onLogoutClick = onLogoutClick,
                            onChangePasswordClick = onChangePasswordClick,
                            onSupportClick = onSupportClick,
                            onAboutClick = onAboutClick
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GoldPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    com.fruex.beerwall.ui.theme.BeerWallTheme {
        MainScreenContent(
            selectedTab = BottomNavItem.Balance.route,
            onTabSelected = {},
            balances = listOf(
                PremisesBalance(
                    premisesId = 1,
                    premisesName = "Pub Centrum",
                    balance = 45.50,
                    loyaltyPoints = 120
                ),
                PremisesBalance(
                    premisesId = 2,
                    premisesName = "Bar przy Rynku",
                    balance = 12.00,
                    loyaltyPoints = 50
                )
            ),
            isBalanceRefreshing = false,
            onBalanceRefresh = {},
            cards = emptyList(),
            isCardsRefreshing = false,
            onCardsRefresh = {},
            onToggleCardStatus = {},
            onDeleteCard = {},
            transactionGroups = emptyList(),
            isHistoryRefreshing = false,
            onHistoryRefresh = {},
            userProfile = UserProfile(name = "Jan Kowalski"),
            tiltAngle = 0f,
            onLogoutClick = {},
            onAddFundsClick = {},
            onAddLocationClick = {},
            onAddCardClick = {},
            onChangePasswordClick = {},
            onSupportClick = {},
            onAboutClick = {}
        )
    }
}
