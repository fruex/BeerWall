package org.fruex.beerwall.ui.screens.profile

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
import org.fruex.beerwall.ui.components.BeerWallButton
import org.fruex.beerwall.ui.components.BeerWallTextField
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.DarkBackground
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    onResetPassword: (email: String, resetCode: String, newPassword: String) -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var resetCode by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val isValid = email.isNotBlank() && email.contains("@") &&
                  resetCode.isNotBlank() &&
                  newPassword.isNotBlank() &&
                  newPassword == confirmPassword

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Resetowanie hasła",
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
                text = "Wprowadź dane, aby zresetować hasło",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            BeerWallTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Adres email",
                modifier = Modifier.fillMaxWidth()
            )

            BeerWallTextField(
                value = resetCode,
                onValueChange = { resetCode = it },
                placeholder = "Kod resetujący",
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

            Spacer(modifier = Modifier.height(8.dp))

            BeerWallButton(
                text = "Zresetuj hasło",
                onClick = { onResetPassword(email, resetCode, newPassword) },
                enabled = isValid
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
            onResetPassword = { _, _, _ -> }
        )
    }
}
