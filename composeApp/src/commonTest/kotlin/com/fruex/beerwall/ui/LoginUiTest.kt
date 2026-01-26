package com.fruex.beerwall.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import com.fruex.beerwall.ui.screens.auth.AuthMode
import com.fruex.beerwall.ui.screens.auth.AuthScreen
import com.fruex.beerwall.ui.theme.BeerWallTheme
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.Ignore

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = "src/androidMain/AndroidManifest.xml")
class LoginUiTest {

    @Ignore // Ignoring until Robolectric environment is fully configured for ComponentActivity
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
