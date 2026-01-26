package com.fruex.beerwall.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.domain.validation.PasswordValidator
import com.fruex.beerwall.ui.components.BeerWallButton
import com.fruex.beerwall.ui.components.BeerWallTextField
import com.fruex.beerwall.ui.theme.BeerWallTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChangePasswordScreen(
    onDismiss: () -> Unit,
    onChangePassword: (oldPassword: String, newPassword: String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val passwordValidation = remember(newPassword) { PasswordValidator.validate(newPassword) }
    
    val passwordsMatch = newPassword.isNotBlank() && newPassword == confirmPassword
    val isOldPasswordValid = oldPassword.isNotEmpty()

    val isValid = passwordValidation.isValid && passwordsMatch && isOldPasswordValid

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp) // Bottom padding for sheet
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Zmiana hasła",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))

        Text(
            text = "Wprowadź stare i nowe hasło",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        BeerWallTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            placeholder = "Stare hasło",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

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

        if (newPassword.isNotEmpty() && !passwordValidation.isValid) {
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
            onClick = { onChangePassword(oldPassword, newPassword) },
            enabled = isValid && !isLoading
        )
    }
}

@Preview
@Composable
fun ChangePasswordScreenPreview() {
    BeerWallTheme {
        ChangePasswordScreen(
            onDismiss = {},
            onChangePassword = { _, _ -> }
        )
    }
}
