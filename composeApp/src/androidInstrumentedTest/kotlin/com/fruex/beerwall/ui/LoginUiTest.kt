package com.fruex.beerwall.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fruex.beerwall.ui.screens.auth.AuthMode
import com.fruex.beerwall.ui.screens.auth.AuthScreen
import com.fruex.beerwall.ui.theme.BeerWallTheme
import org.junit.runner.RunWith
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class LoginUiTest {

    @Test
    fun testLoginFlow_UI_Elements_Exist() = runComposeUiTest {
        setContent {
            BeerWallTheme {
                AuthScreen(
                    mode = AuthMode.LOGIN,
                    onAuthClick = { _, _ -> },
                    onGoogleSignInClick = {},
                    onToggleModeClick = {},
                    onForgotPasswordClick = {}
                )
            }
        }

        // Verify Header
        onNodeWithText("Zaloguj do konta").assertIsDisplayed()

        // Click Email Option
        onNodeWithText("Kontynuuj z e-mailem").performClick()

        // Verify Email Field
        onNodeWithText("E-mail").assertIsDisplayed()
        onNodeWithText("E-mail").performTextInput("test@example.com")

        // Click First "Zaloguj" button to proceed to password
        // Note: There might be multiple "Zaloguj" texts (e.g. title?). Button usually has role.
        // The title is "Zaloguj do konta", button is "Zaloguj".
        onNodeWithText("Zaloguj").performClick()

        // Verify Password Field appears
        onNodeWithText("Hasło").assertIsDisplayed()
    }
}
