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
import org.fruex.beerwall.ui.models.CardItem
import org.fruex.beerwall.ui.models.LocationBalance
import org.fruex.beerwall.ui.models.TransactionGroup
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
    balances: List<LocationBalance> = emptyList(),
    onAddFundsClick: (String) -> Unit = {},
    onAddLocationClick: () -> Unit = {},

    // Cards callbacks
    cards: List<CardItem> = emptyList(),
    onAddCardClick: () -> Unit = {},
    onToggleCardStatus: (String) -> Unit = {},
    onDeleteCard: (String) -> Unit = {},

    // History data
    transactionGroups: List<TransactionGroup> = emptyList(),

    // Profile data & callbacks
    userProfile: UserProfile = UserProfile("", "", "", 0, 0),
    onLogoutClick: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavItem.Balance.route) }

    val items = listOf(
        BottomNavItem.Balance,
        BottomNavItem.Cards,
        BottomNavItem.History,
        BottomNavItem.Profile
    )

    BeerWallTheme {
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
                            transactionGroups = transactionGroups
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
}
