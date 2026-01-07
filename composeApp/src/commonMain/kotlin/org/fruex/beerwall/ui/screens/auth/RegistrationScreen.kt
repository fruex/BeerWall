package org.fruex.beerwall.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.fruex.beerwall.ui.components.*
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.DarkBackground
import org.fruex.beerwall.ui.theme.GoldPrimary
import org.fruex.beerwall.ui.theme.TextSecondary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RegistrationScreen(
    onRegisterClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

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
            subtitle = "Utwórz konto"
        )

        Spacer(modifier = Modifier.height(40.dp))

        BeerWallTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "E-mail",
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        BeerWallTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Hasło",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        BeerWallButton(
            text = "Utwórz konto",
            onClick = { onRegisterClick(email, password) },
            enabled = email.isNotBlank() && password.isNotBlank()
        )

        Spacer(modifier = Modifier.height(24.dp))

        SocialDivider()

        Spacer(modifier = Modifier.height(24.dp))

        BeerWallOutlinedButton(
            text = "Google",
            onClick = onGoogleSignInClick,
            icon = Icons.Default.AccountCircle
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Masz już konto? ",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            TextButton(
                onClick = onLoginClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Zaloguj się",
                    color = GoldPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
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
