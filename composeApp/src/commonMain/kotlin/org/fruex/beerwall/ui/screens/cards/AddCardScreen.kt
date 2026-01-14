package org.fruex.beerwall.ui.screens.cards

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.fruex.beerwall.ui.components.*
import org.fruex.beerwall.ui.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    scannedCardId: String?,
    isNfcEnabled: Boolean,
    onBackClick: () -> Unit,
    onStartScanning: () -> Unit,
    onCardNameChanged: (String) -> Unit,
    onSaveCard: (name: String, cardId: String) -> Unit,
) {
    var cardName by rememberSaveable { mutableStateOf("") }
    val canSave = scannedCardId != null

    LaunchedEffect(Unit) {
        onStartScanning()
    }

    BeerWallTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        text = "Dodaj nową kartę",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            scannedCardId?.let { cardId ->
                                if (canSave) {
                                    onSaveCard(cardName.ifBlank { "Karta NFC" }, cardId)
                                }
                            }
                        },
                        enabled = canSave
                    ) {
                        Text(
                            text = "Zapisz",
                            color = if (canSave) GoldPrimary else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isNfcEnabled) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "NFC jest wyłączone. Włącz NFC w ustawieniach telefonu, aby zeskanować kartę.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Text(
                    text = "Nazwa karty (opcjonalnie)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                BeerWallTextField(
                    value = cardName,
                    onValueChange = {
                        cardName = it
                        onCardNameChanged(it)
                    },
                    placeholder = "np. Moja karta, Karta służbowa",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // NFC Scanning Area
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNfcEnabled) GoldPrimary else TextSecondary
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NFCIcon(isScanning = scannedCardId == null && isNfcEnabled)

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = if (scannedCardId != null) "Karta wykryta!" else if (isNfcEnabled) "Gotowe do skanowania" else "NFC wyłączone",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (scannedCardId != null) {
                                "Karta została pomyślnie zeskanowana $scannedCardId"
                            } else if (isNfcEnabled) {
                                "Przytrzymaj kartę NFC blisko urządzenia, aby ją zarejestrować"
                            } else {
                                "Włącz moduł NFC w ustawieniach"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkBackground.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Info Card
                BeerWallInfoCard(
                    icon = Icons.Default.Info,
                    description = "Upewnij się, że NFC jest włączone na urządzeniu. Karta zostanie połączona z Twoim kontem i można jej używać przy każdym kranie Beer Wall."
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (scannedCardId != null) {
                    BeerWallButton(
                        text = "Zapisz kartę",
                        onClick = {
                            onSaveCard(
                                cardName.ifBlank { "Karta NFC" },
                                scannedCardId
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NFCIcon(isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .background(
                color = DarkBackground.copy(alpha = 0.2f),
                shape = CircleShape
            )
            .scale(if (isScanning) scale else 1f),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Nfc,
            contentDescription = null,
            tint = DarkBackground,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Preview
@Composable
fun AddCardScreenPreview() {
    BeerWallTheme {
        AddCardScreen(
            scannedCardId = null,
            isNfcEnabled = true,
            onBackClick = {},
            onStartScanning = {},
            onCardNameChanged = {},
            onSaveCard = { _, _ -> }
        )
    }
}
