package com.igibeer.beerwall.ui.screens.cards

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.igibeer.beerwall.ui.components.BeerWallButton
import com.igibeer.beerwall.ui.components.BeerWallTextField
import com.igibeer.beerwall.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    isScanning: Boolean,
    scannedCardId: String?,
    onBackClick: () -> Unit,
    onStartScanning: () -> Unit,
    onCardNameChanged: (String) -> Unit,
    onSaveCard: (name: String, cardId: String) -> Unit,
) {
    var cardName by remember { mutableStateOf("") }
    val canSave = scannedCardId != null && cardName.isNotBlank()

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
                            imageVector = Icons.Default.ArrowBack,
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
                                    onSaveCard(cardName, cardId)
                                }
                            }
                        },
                        enabled = canSave
                    ) {
                        Text(
                            text = "Anuluj",
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
                Spacer(modifier = Modifier.height(16.dp))

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
                        containerColor = GoldPrimary
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NFCIcon(isScanning = isScanning)

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = if (scannedCardId != null) "Karta wykryta!" else "Gotowe do skanowania",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (scannedCardId != null) {
                                "Karta została pomyślnie zeskanowana"
                            } else {
                                "Przytrzymaj kartę NFC blisko urządzenia, aby ją zarejestrować"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkBackground.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )

                        if (scannedCardId == null && !isScanning) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onStartScanning,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkBackground,
                                    contentColor = GoldPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Rozpocznij skanowanie NFC",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Upewnij się, że NFC jest włączone na urządzeniu. Karta zostanie połączona z Twoim kontem i można jej używać przy każdym kranie Beer Wall.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }

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

                Spacer(modifier = Modifier.height(24.dp))
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
