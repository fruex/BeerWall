package com.fruex.beerwall.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.ui.components.BeerWallButton
import com.fruex.beerwall.ui.theme.BeerWallTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SupportScreen(
    onDismiss: () -> Unit,
    onSendMessage: (message: String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    successMessage: String? = null,
    onClearState: () -> Unit = {}
) {
    var message by rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    DisposableEffect(Unit) {
        onDispose {
            onClearState()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            message = ""
            val job = launch {
                snackbarHostState.showSnackbar(it)
            }
            delay(2000)
            job.cancel()
            onDismiss()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pomoc i wsparcie",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))

            Text(
                text = "Masz pytanie lub problem? Wyślij nam wiadomość, a nasz zespół odpowie najszybciej jak to możliwe.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = message,
                onValueChange = {
                    if (it.length <= 200) message = it
                },
                placeholder = {
                    Text("Opisz swój problem lub pytanie...")
                },
                supportingText = {
                    Text(
                        text = "${message.length}/200",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                ),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            BeerWallButton(
                text = if (isLoading) "Wysyłanie..." else "Wyślij wiadomość",
                onClick = {
                    onSendMessage(message)
                },
                enabled = message.isNotBlank() && !isLoading,
                isLoading = isLoading,
                icon = Icons.AutoMirrored.Filled.Send
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
fun SupportScreenPreview() {
    BeerWallTheme {
        SupportScreen(
            onDismiss = {},
            onSendMessage = {}
        )
    }
}
