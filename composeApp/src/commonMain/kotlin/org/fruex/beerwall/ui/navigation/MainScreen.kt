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
import org.fruex.beerwall.ui.models.DailyTransactions
import org.fruex.beerwall.ui.models.UserCard
import org.fruex.beerwall.ui.models.UserProfile
import org.fruex.beerwall.ui.models.VenueBalance
import org.fruex.beerwall.ui.screens.balance.BalanceScreen
import org.fruex.beerwall.ui.screens.cards.CardsScreen
import org.fruex.beerwall.ui.screens.history.HistoryScreen
import org.fruex.beerwall.ui.screens.profile.ProfileScreen
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
 * @param balances Lista sald (stan).
 * @param onAddFundsClick Callback do ekranu doładowania.
 * @param onAddLocationClick Callback do dodawania lokalizacji.
 * @param onRefreshBalance Callback odświeżania salda.
 * @param cards Lista kart (stan).
 * @param onAddCardClick Callback do dodawania karty.
 * @param onToggleCardStatus Callback zmiany statusu karty.
 * @param onDeleteCard Callback usuwania karty.
 * @param transactionGroups Grupy transakcji (stan).
 * @param onRefreshHistory Callback odświeżania historii.
 * @param isRefreshing Flaga odświeżania (stan).
 * @param userProfile Profil użytkownika (stan).
 * @param onLogoutClick Callback wylogowania.
 * @param onChangePasswordClick Callback zmiany hasła.
 * @param onSupportClick Callback pomocy.
 * @param onAboutClick Callback "O aplikacji".
 */
@Composable
fun MainScreen(
    // Balance callbacks
    balances: List<VenueBalance> = emptyList(),
    onAddFundsClick: (premisesId: Int) -> Unit = {},
    onAddLocationClick: () -> Unit = {},
    onRefreshBalanceClick: () -> Unit = {},

    // Cards callbacks
    cards: List<UserCard> = emptyList(),
    onAddCardClick: () -> Unit = {},
    onToggleCardStatusClick: (String) -> Unit = {},
    onDeleteCardClick: (String) -> Unit = {},

    // History data
    transactionGroups: List<DailyTransactions> = emptyList(),
    onRefreshHistoryClick: () -> Unit = {},

    // Global state
    isRefreshing: Boolean = false,

    // Profile data & callbacks
    userProfile: UserProfile = UserProfile("", "", ""),
    onLogoutClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
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
            BottomNavItem.Balance.route -> onRefreshBalanceClick()
            BottomNavItem.History.route -> onRefreshHistoryClick()
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
                        onRefresh = onRefreshBalanceClick,
                        onAddFundsClick = onAddFundsClick,
                        onAddLocationClick = onAddLocationClick
                    )
                }
                BottomNavItem.Cards.route -> {
                    CardsScreen(
                        cards = cards,
                        onAddCardClick = onAddCardClick,
                        onToggleCardStatus = onToggleCardStatusClick,
                        onDeleteCard = onDeleteCardClick
                    )
                }
                BottomNavItem.History.route -> {
                    HistoryScreen(
                        transactionGroups = transactionGroups,
                        isRefreshing = isRefreshing,
                        onRefresh = onRefreshHistoryClick
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
