package org.fruex.beerwall.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.fruex.beerwall.ui.components.*
import org.fruex.beerwall.ui.theme.DarkBackground
import org.fruex.beerwall.ui.theme.GoldPrimary
import org.fruex.beerwall.ui.theme.TextSecondary

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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(44.dp))

        AppLogo()

        Spacer(modifier = Modifier.height(32.dp))

        AuthHeader(
            title = "Igi Beer System",
            subtitle = "Utwórz konto"
        )

        Spacer(modifier = Modifier.height(24.dp))

        BeerWallTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "E-mail",
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

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

        Spacer(modifier = Modifier.height(8.dp))

        BeerWallButton(
            text = "Utwórz konto",
            onClick = { onRegisterClick(email, password) },
            enabled = email.isNotBlank() && password.isNotBlank()
        )

        Spacer(modifier = Modifier.height(8.dp))

        SocialDivider()

        Spacer(modifier = Modifier.height(8.dp))

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
