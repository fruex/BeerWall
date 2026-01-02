package com.igibeer.beerwall.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.igibeer.beerwall.ui.models.CardItem
import com.igibeer.beerwall.ui.models.LocationBalance
import com.igibeer.beerwall.ui.models.TransactionGroup
import com.igibeer.beerwall.ui.models.UserProfile
import com.igibeer.beerwall.ui.screens.balance.BalanceScreen
import com.igibeer.beerwall.ui.screens.cards.CardsScreen
import com.igibeer.beerwall.ui.screens.history.HistoryScreen
import com.igibeer.beerwall.ui.screens.profile.ProfileScreen
import com.igibeer.beerwall.ui.theme.BeerWallTheme
import com.igibeer.beerwall.ui.theme.CardBackground
import com.igibeer.beerwall.ui.theme.GoldPrimary

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
    userProfile: UserProfile? = null,
    onLogoutClick: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(BottomNavItem.Balance.route) }

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
