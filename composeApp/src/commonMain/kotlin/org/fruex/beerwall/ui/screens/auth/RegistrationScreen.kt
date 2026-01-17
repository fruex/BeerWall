package org.fruex.beerwall.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import beerwall.composeapp.generated.resources.Res
import beerwall.composeapp.generated.resources.ic_apple
import beerwall.composeapp.generated.resources.ic_facebook
import beerwall.composeapp.generated.resources.ic_google
import org.fruex.beerwall.ui.components.*
import org.fruex.beerwall.ui.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Ekran rejestracji (wrapper dla AuthScreen).
 *
 * @param onRegisterClick Callback rejestracji emailem.
 * @param onGoogleSignInClick Callback rejestracji Google.
 * @param onLoginClick Callback przejścia do logowania.
 * @param isLoading Flaga ładowania.
 */
@Composable
fun RegistrationScreen(
    onRegisterClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean = false
) {
    AuthScreen(
        mode = AuthMode.REGISTER,
        onAuthClick = onRegisterClick,
        onGoogleSignInClick = onGoogleSignInClick,
        onToggleModeClick = onLoginClick,
        isLoading = isLoading
    )
}

@Preview
@Composable
fun RegistrationScreenPreview() {
    BeerWallTheme {
        RegistrationScreen(
            onRegisterClick = { _, _ -> },
            onGoogleSignInClick = {},
            onLoginClick = {}
        )
    }
}
