package com.fruex.beerwall.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.fruex.beerwall.ui.models.DailyTransactions
import com.fruex.beerwall.ui.models.Transaction
import com.fruex.beerwall.ui.screens.history.HistoryScreen
import com.fruex.beerwall.ui.theme.BeerWallTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HistoryUiTest {

    @Test
    fun testHistoryScreen_UI_Elements_Exist_WithTransactions() = runComposeUiTest {
        val groups = listOf(
            DailyTransactions(
                date = "Dzisiaj",
                transactions = listOf(
                    Transaction(
                        transactionId = 1,
                        commodityName = "Piwo Jasne",
                        formattedPrice = "-12.50 zł",
                        formattedCapacity = "500 ml",
                        formattedDetails = "Pub Warszawski o 18:30"
                    )
                )
            ),
            DailyTransactions(
                date = "Wczoraj",
                transactions = listOf(
                    Transaction(
                        transactionId = 2,
                        commodityName = "Piwo Ciemne",
                        formattedPrice = "-15.00 zł",
                        formattedCapacity = "500 ml",
                        formattedDetails = "Pub Krakowski o 20:15"
                    )
                )
            )
        )

        setContent {
            BeerWallTheme {
                HistoryScreen(
                    transactionGroups = groups,
                    isRefreshing = false,
                    onRefresh = {}
                )
            }
        }

        // Verify Headers
        onNodeWithText("Dzisiaj").assertIsDisplayed()
        onNodeWithText("Wczoraj").assertIsDisplayed()

        // Verify Transactions
        onNodeWithText("Piwo Jasne").assertIsDisplayed()
        onNodeWithText("-12.50 zł").assertIsDisplayed()
        onNodeWithText("500 ml").assertIsDisplayed()
        onNodeWithText("Pub Warszawski o 18:30").assertIsDisplayed()

        onNodeWithText("Piwo Ciemne").assertIsDisplayed()
        onNodeWithText("-15.00 zł").assertIsDisplayed()
    }
}
