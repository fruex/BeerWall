package com.fruex.beerwall.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.fruex.beerwall.ui.models.UserCard
import com.fruex.beerwall.ui.screens.cards.CardsScreen
import com.fruex.beerwall.ui.theme.BeerWallTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CardsUiTest {

    @Test
    fun testCardsScreen_UI_Elements_Exist_WithCards() = runComposeUiTest {
        val cards = listOf(
            UserCard(
                cardGuid = "uuid1",
                description = "Karta wirtualna",
                isActive = false,
                isPhysical = false
            ),
            UserCard(
                cardGuid = "uuid2",
                description = "Karta fizyczna",
                isActive = true,
                isPhysical = true
            )
        )

        setContent {
            BeerWallTheme {
                CardsScreen(
                    cards = cards,
                    onAddCardClick = {},
                    onToggleCardStatus = {}
                )
            }
        }

        // Verify Header
        onNodeWithText("Moje karty").assertIsDisplayed()
        onNodeWithText("2 karty połączone").assertIsDisplayed()

        // Verify Cards
        onNodeWithText("Karta wirtualna").assertIsDisplayed()
        onNodeWithText("Karta fizyczna").assertIsDisplayed()
        onNodeWithText("Aktywna").assertIsDisplayed()

        // Verify Add Button
        onNodeWithText("Dodaj nową kartę").assertIsDisplayed()
    }
}
