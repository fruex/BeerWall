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
 * Ekran logowania.
 *
 * Umożliwia logowanie za pomocą emaila/hasła oraz kont społecznościowych.
 *
 * @param onLoginClick Callback logowania emailem.
 * @param onGoogleSignInClick Callback logowania Google.
 * @param onRegisterClick Callback przejścia do rejestracji.
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
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showEmailLogin by rememberSaveable { mutableStateOf(false) }
    var showPasswordStep by rememberSaveable { mutableStateOf(false) }

    LoadingDialog(
        isVisible = isLoading,
        title = "Logowanie..."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppLogo()

        Spacer(modifier = Modifier.height(48.dp))

        AuthHeader(
            title = "Igi Beer System",
            subtitle = "Zaloguj do konta"
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Google Login Button
        SocialLoginButton(
            text = "Kontynuuj z Google",
            onClick = onGoogleSignInClick,
            iconRes = Res.drawable.ic_google,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Facebook Login Button (disabled)
        SocialLoginButton(
            text = "Kontynuuj z Facebook",
            onClick = { },
            iconRes = Res.drawable.ic_facebook,
            enabled = false
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Apple Login Button (disabled)
        SocialLoginButton(
            text = "Kontynuuj z Apple",
            onClick = { },
            iconRes = Res.drawable.ic_apple,
            enabled = false
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = TextSecondary.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email Login Toggle
        if (!showEmailLogin) {
            OutlinedButton(
                onClick = { showEmailLogin = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextPrimary
                ),
                border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.3f)),
                contentPadding = PaddingValues(16.dp),
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Kontynuuj z e-mailem",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // Email Login Form
            BeerWallTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "E-mail",
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && !showPasswordStep
            )

            if (!showPasswordStep) {
                Spacer(modifier = Modifier.height(24.dp))

                BeerWallButton(
                    text = "Zaloguj",
                    onClick = { showPasswordStep = true },
                    enabled = email.isNotBlank() && !isLoading
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                BeerWallTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Hasło",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                BeerWallButton(
                    text = "Zaloguj",
                    onClick = { onLoginClick(email, password) },
                    enabled = password.isNotBlank() && !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { onForgotPasswordClick(email) },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Zapomniałeś hasła?",
                        color = GoldPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Nie masz konta? ",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            TextButton(
                onClick = onRegisterClick,
                contentPadding = PaddingValues(0.dp),
                enabled = !isLoading
            ) {
                Text(
                    text = "Utwórz konto",
                    color = GoldPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
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
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
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
