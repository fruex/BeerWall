package org.fruex.beerwall.ui.screens.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fruex.beerwall.ui.components.BeerWallButton
import org.fruex.beerwall.ui.models.UserCard
import org.fruex.beerwall.ui.theme.*

@Composable
fun CardsScreen(
    cards: List<UserCard>,
    onAddCardClick: () -> Unit,
    onToggleCardStatus: (String) -> Unit,
    onDeleteCard: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Igi Beer System",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Twój cyfrowy portfel piwny",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
        ) {
            item(key = "header") {
                Text(
                    text = "Moje karty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${cards.size} karty połączone",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(
                items = cards,
                key = { it.id }
            ) { card ->
                CardItemView(
                    card = card,
                    onToggleStatus = { onToggleCardStatus(card.id) },
                    onDelete = { onDeleteCard(card.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item(key = "add_card_button") {
                Spacer(modifier = Modifier.height(8.dp))
                BeerWallButton(
                    text = "Dodaj nową kartę",
                    onClick = onAddCardClick,
                )
            }

            item(key = "nfc_info") {
                Spacer(modifier = Modifier.height(24.dp))
                NFCInfoCard()
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CardItemView(
    card: UserCard,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
) {
    // Determine card style based on type
    val cardBackground = if (card.isPhysical) {
        CardBackground
    } else {
        // Glass effect for virtual cards - transparent with golden tint
        GoldPrimary.copy(alpha = 0.12f)
    }

    val cardModifier = if (card.isPhysical) {
        Modifier.fillMaxWidth()
    } else {
        // Add border for glass effect
        Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = GoldPrimary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Card icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = if (card.isPhysical) {
                                GoldPrimary
                            } else {
                                // Lighter gold for virtual cards
                                GoldPrimary.copy(alpha = 0.25f)
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .then(
                            if (!card.isPhysical) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = GoldPrimary.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = if (card.isPhysical) DarkBackground else GoldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = card.id,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (card.isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (card.isActive) Success else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (card.isActive) "Aktywna" else "Nieaktywna",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (card.isActive) Success else TextSecondary
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (card.isPhysical) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Usuń kartę",
                            tint = TextSecondary
                        )
                    }
                }
                TextButton(
                    onClick = onToggleStatus,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (card.isActive) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (card.isActive) "Wyłącz" else "Włącz",
                            style = MaterialTheme.typography.labelLarge,
                            color = GoldPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NFCInfoCard() {
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = GoldPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Nfc,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Karty NFC",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Twoje fizyczne karty są połączone z Twoim kontem. Po prostu dotknij dowolnej karty przy kranie Beer Wall, aby nalać i zapłacić.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
