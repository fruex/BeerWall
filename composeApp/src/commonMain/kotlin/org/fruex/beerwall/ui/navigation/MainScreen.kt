package org.fruex.beerwall.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.fruex.beerwall.ui.models.UserCard
import org.fruex.beerwall.ui.models.VenueBalance
import org.fruex.beerwall.ui.models.DailyTransactions
import org.fruex.beerwall.ui.models.UserProfile
import org.fruex.beerwall.ui.screens.balance.BalanceScreen
import org.fruex.beerwall.ui.screens.cards.CardsScreen
import org.fruex.beerwall.ui.screens.history.HistoryScreen
import org.fruex.beerwall.ui.screens.profile.ProfileScreen
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.CardBackground
import org.fruex.beerwall.ui.theme.GoldPrimary

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
        unselectedIcon = Icons.Outlined.AccountBalanceWallet
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

@Composable
fun MainScreen(
    // Balance callbacks
    balances: List<VenueBalance> = emptyList(),
    onAddFundsClick: (venueId: Int) -> Unit = {},
    onAddLocationClick: () -> Unit = {},
    onRefreshBalance: () -> Unit = {},

    // Cards callbacks
    cards: List<UserCard> = emptyList(),
    onAddCardClick: () -> Unit = {},
    onToggleCardStatus: (String) -> Unit = {},
    onDeleteCard: (String) -> Unit = {},

    // History data
    transactionGroups: List<DailyTransactions> = emptyList(),
    onRefreshHistory: () -> Unit = {},

    // Global state
    isRefreshing: Boolean = false,

    // Profile data & callbacks
    userProfile: UserProfile = UserProfile("", "", "", 0, 0),
    onLogoutClick: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavItem.Balance.route) }

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
            BottomNavItem.Balance.route -> onRefreshBalance()
            BottomNavItem.History.route -> onRefreshHistory()
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
                        balances = balances,
                        isRefreshing = isRefreshing,
                        onRefresh = onRefreshBalance,
                        onAddFundsClick = onAddFundsClick,
                        onAddLocationClick = onAddLocationClick
                    )
                }
                BottomNavItem.Cards.route -> {
                    CardsScreen(
                        cards = cards,
                        onAddCardClick = onAddCardClick,
                        onToggleCardStatus = onToggleCardStatus,
                        onDeleteCard = onDeleteCard
                    )
                }
                BottomNavItem.History.route -> {
                    HistoryScreen(
                        transactionGroups = transactionGroups,
                        isRefreshing = isRefreshing,
                        onRefresh = onRefreshHistory
                    )
                }
                BottomNavItem.Profile.route -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        onLogoutClick = onLogoutClick
                    )
                }
            }
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun MainScreenPreview() {
    BeerWallTheme {
        MainScreen(
            balances = listOf(
                org.fruex.beerwall.ui.models.VenueBalance(1, "Bar Centrum", 45.50, 120),
                org.fruex.beerwall.ui.models.VenueBalance(2, "Pub Piwnica", 12.00, 45)
            ),
            cards = listOf(
                org.fruex.beerwall.ui.models.UserCard("1234 5678 9012", "Moja karta NFC", true, true)
            ),
            transactionGroups = listOf(
                org.fruex.beerwall.ui.models.DailyTransactions(
                    date = "Dzisiaj",
                    transactions = listOf(
                        org.fruex.beerwall.ui.models.Transaction("1", "Pilsner", "20:30", -14.50, 500)
                    )
                )
            ),
            userProfile = org.fruex.beerwall.ui.models.UserProfile(
                name = "Jan Kowalski",
                email = "jan@example.com",
                initials = "JK",
                activeCards = 1,
                loyaltyPoints = 120
            ),
            isRefreshing = false
        )
    }
}
