package com.fruex.beerwall.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.fruex.beerwall.ui.models.PremisesBalance
import com.fruex.beerwall.ui.screens.balance.BalanceScreen
import com.fruex.beerwall.ui.theme.BeerWallTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class BalanceUiTest {

    @Test
    fun testBalanceScreen_UI_Elements_Exist() = runComposeUiTest {
        val balances = listOf(
            PremisesBalance(
                premisesId = 1,
                premisesName = "Pub Centrum",
                balance = 45.5,
                loyaltyPoints = 120
            ),
            PremisesBalance(
                premisesId = 2,
                premisesName = "Bar przy Rynku",
                balance = 12.0,
                loyaltyPoints = 50
            )
        )

        setContent {
            BeerWallTheme {
                BalanceScreen(
                    balances = balances,
                    isRefreshing = false,
                    onRefresh = {},
                    onAddFundsClick = {},
                    onAddLocationClick = {}
                )
            }
        }

        // Verify Headers
        onNodeWithText("Dostępne saldo").assertIsDisplayed()

        // Verify Premises Name and Balance
        onNodeWithText("Pub Centrum").assertIsDisplayed()
        onNodeWithText("45.5 zł").assertIsDisplayed()
        onNodeWithText("120 pkt").assertIsDisplayed()

        onNodeWithText("Bar przy Rynku").assertIsDisplayed()
        onNodeWithText("12.0 zł").assertIsDisplayed()
    }
}
