package com.fruex.beerwall.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.ui.components.BeerWallButton
import com.fruex.beerwall.ui.components.BeerWallTextField
import com.fruex.beerwall.ui.theme.BeerWallTheme
import com.fruex.beerwall.ui.theme.DarkBackground
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    onChangePassword: (newPassword: String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    // Password Policy:
    // Min 6 chars, 1 digit, 1 lowercase, 1 uppercase, 1 special char.
    // Unique chars: >= 1 (always true if length > 0)
    val hasDigit = newPassword.any { it.isDigit() }
    val hasLowerCase = newPassword.any { it.isLowerCase() }
    val hasUpperCase = newPassword.any { it.isUpperCase() }
    // NonAlphanumeric: anything that is not a letter or digit
    val hasSpecialChar = newPassword.any { !it.isLetterOrDigit() }
    val isLengthValid = newPassword.length >= 6
    val passwordsMatch = newPassword.isNotBlank() && newPassword == confirmPassword

    val isValid = isLengthValid && hasDigit && hasLowerCase && hasUpperCase && hasSpecialChar && passwordsMatch

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Zmiana hasła",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wróć"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Wprowadź nowe hasło",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            BeerWallTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = "Nowe hasło",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            BeerWallTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Potwierdź hasło",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (newPassword.isNotEmpty() && !isValid) {
                Text(
                    text = "Hasło musi mieć min. 6 znaków, zawierać małą i wielką literę, cyfrę oraz znak specjalny.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            BeerWallButton(
                text = if (isLoading) "Zmieniam..." else "Zmień hasło",
                onClick = { onChangePassword(newPassword) },
                enabled = isValid && !isLoading
            )
        }
    }
}

@Preview
@Composable
fun ChangePasswordScreenPreview() {
    BeerWallTheme {
        ChangePasswordScreen(
            onBackClick = {},
            onChangePassword = { _ -> }
        )
    }
}
