package com.fruex.beerwall.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.ui.components.BeerWallButton
import com.fruex.beerwall.ui.theme.BeerWallTheme
import com.fruex.beerwall.ui.theme.DarkBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onBackClick: () -> Unit,
    onSendMessage: (message: String) -> Unit,
) {
    var message by rememberSaveable { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pomoc i wsparcie",
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
                text = "Masz pytanie lub problem? Wyślij nam wiadomość, a nasz zespół odpowie najszybciej jak to możliwe.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (showSuccessMessage) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "✓ Wiadomość została wysłana do zespołu wsparcia BeerWall",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = {
                    Text("Opisz swój problem lub pytanie...")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            BeerWallButton(
                text = if (isSending) "Wysyłanie..." else "Wyślij wiadomość",
                onClick = {
                    scope.launch {
                        isSending = true
                        showSuccessMessage = false
                        delay(1500)
                        onSendMessage(message)
                        message = ""
                        isSending = false
                        showSuccessMessage = true
                        delay(5000)
                        showSuccessMessage = false
                    }
                },
                enabled = message.isNotBlank() && !isSending,
                icon = Icons.AutoMirrored.Filled.Send
            )
        }
    }
}

@Preview
@Composable
fun SupportScreenPreview() {
    BeerWallTheme {
        SupportScreen(
            onBackClick = {},
            onSendMessage = {}
        )
    }
}
