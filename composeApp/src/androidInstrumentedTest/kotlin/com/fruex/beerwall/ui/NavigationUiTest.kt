package com.fruex.beerwall.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.fruex.beerwall.ui.navigation.BottomNavItem
import com.fruex.beerwall.ui.navigation.MainScreenContent
import com.fruex.beerwall.domain.model.UserProfile
import com.fruex.beerwall.ui.theme.BeerWallTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class NavigationUiTest {

    @Test
    fun testNavigation_TabSwitching() = runComposeUiTest {
        setContent {
            var selectedTab by remember { mutableStateOf(BottomNavItem.Balance.route) }

            BeerWallTheme {
                MainScreenContent(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    balances = emptyList(),
                    isBalanceRefreshing = false,
                    onBalanceRefresh = {},
                    cards = emptyList(),
                    isCardsRefreshing = false,
                    onCardsRefresh = {},
                    onToggleCardStatus = {},
                    transactionGroups = emptyList(),
                    isHistoryRefreshing = false,
                    onHistoryRefresh = {},
                    userProfile = UserProfile(name = "Test User"),
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

        // Initial state: Balance tab
        onNodeWithText("Dostępne saldo").assertIsDisplayed()

        // Switch to Cards
        onNodeWithText("Karty").performClick()
        onNodeWithText("Moje karty").assertIsDisplayed()

        // Switch to History
        onNodeWithText("Historia").performClick()
        onNodeWithText("Brak transakcji").assertIsDisplayed()

        // Switch to Profile
        onNodeWithText("Profil").performClick()
        // Profile screen displays "Wyloguj" button/card
        onNodeWithText("Wyloguj").assertIsDisplayed()
    }
}
