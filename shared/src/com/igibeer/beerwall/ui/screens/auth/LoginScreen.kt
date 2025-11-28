package com.igibeer.beerwall.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.igibeer.beerwall.ui.components.BeerWallButton
import com.igibeer.beerwall.ui.components.BeerWallOutlinedButton
import com.igibeer.beerwall.ui.components.BeerWallTextField
import com.igibeer.beerwall.ui.theme.BeerWallTheme
import com.igibeer.beerwall.ui.theme.GoldPrimary
import com.igibeer.beerwall.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    BeerWallTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo placeholder
            Text(
                text = "IGI BEER",
                style = MaterialTheme.typography.displayMedium,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Igi Beer System",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Zaloguj do konta",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                text = "Zaloguj",
                onClick = { onLoginClick(email, password) },
                enabled = email.isNotBlank() && password.isNotBlank()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = TextSecondary.copy(alpha = 0.3f)
                )
                Text(
                    text = "lub kontynuuj z",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = TextSecondary.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BeerWallOutlinedButton(
                text = "Google",
                onClick = onGoogleSignInClick
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Nie masz konta? ",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = onRegisterClick,
                    contentPadding = PaddingValues(0.dp)
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
}
