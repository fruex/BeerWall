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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Ekran logowania (wrapper dla AuthScreen).
 *
 * @param onLoginClick Callback logowania emailem.
 * @param onGoogleSignInClick Callback logowania Google.
 * @param onRegisterClick Callback przejścia do rejestracji.
 * @param onForgotPasswordClick Callback dla zapomnienia hasła.
 * @param isLoading Flaga ładowania.
 */
@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: (email: String) -> Unit,
    isLoading: Boolean = false
) {
    AuthScreen(
        mode = AuthMode.LOGIN,
        onAuthClick = onLoginClick,
        onGoogleSignInClick = onGoogleSignInClick,
        onToggleModeClick = onRegisterClick,
        onForgotPasswordClick = onForgotPasswordClick,
        isLoading = isLoading
    )
}

@Composable
fun SocialLoginButton(
    text: String,
    onClick: () -> Unit,
    iconRes: org.jetbrains.compose.resources.DrawableResource,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (enabled) Color.Transparent else CardBackground.copy(alpha = 0.3f),
            contentColor = if (enabled) TextPrimary else TextSecondary,
            disabledContainerColor = CardBackground.copy(alpha = 0.3f),
            disabledContentColor = TextSecondary
        ),
        border = BorderStroke(
            1.dp,
            if (enabled) TextSecondary.copy(alpha = 0.3f) else TextSecondary.copy(alpha = 0.1f)
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon column - fixed width, centered
            Box(
                modifier = Modifier.width(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text column - centered
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Right spacer to balance icon column (32dp icon + 12dp spacer)
            Spacer(modifier = Modifier.width(44.dp))
        }
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (enabled) Color.Transparent else CardBackground.copy(alpha = 0.3f),
            contentColor = if (enabled) TextPrimary else TextSecondary,
            disabledContainerColor = CardBackground.copy(alpha = 0.3f),
            disabledContentColor = TextSecondary
        ),
        border = BorderStroke(
            1.dp,
            if (enabled) TextSecondary.copy(alpha = 0.3f) else TextSecondary.copy(alpha = 0.1f)
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon column - fixed width, centered
            Box(
                modifier = Modifier.width(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text column - centered
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Right spacer to balance icon column (32dp icon + 12dp spacer)
            Spacer(modifier = Modifier.width(44.dp))
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    BeerWallTheme {
        LoginScreen(
            onLoginClick = { _, _ -> },
            onGoogleSignInClick = {},
            onRegisterClick = {},
            onForgotPasswordClick = {}
        )
    }
}
