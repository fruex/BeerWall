package com.fruex.beerwall.ui.screens.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.ui.components.AppHeader
import com.fruex.beerwall.ui.components.BackgroundGlow
import com.fruex.beerwall.ui.components.BeerWallButton
import com.fruex.beerwall.ui.components.BeerWallInfoCard
import com.fruex.beerwall.ui.components.SectionHeader
import com.fruex.beerwall.ui.models.UserCard
import com.fruex.beerwall.ui.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview

private val VirtualCardBackground = GoldPrimary.copy(alpha = 0.12f)
private val VirtualCardBorderColor = GoldPrimary.copy(alpha = 0.3f)
private val VirtualCardIconBackground = GoldPrimary.copy(alpha = 0.25f)
private val VirtualCardIconBorderColor = GoldPrimary.copy(alpha = 0.4f)
private val InfoCardIconBackground = GoldPrimary.copy(alpha = 0.2f)

/**
 * Ekran zarządzania kartami.
 *
 * Umożliwia przeglądanie, dodawanie, usuwanie i zmianę statusu kart użytkownika.
 *
 * @param cards Lista kart.
 * @param isRefreshing Flaga odświeżania.
 * @param onRefresh Callback odświeżania.
 * @param onAddCardClick Callback do dodawania karty.
 * @param onToggleCardStatus Callback zmiany statusu karty.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    cards: List<UserCard>,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onAddCardClick: () -> Unit,
    onToggleCardStatus: (String) -> Unit,
) {
    // ⚡ Bolt Optimization: Hoist dialog state out of LazyColumn to prevent
    // creating state per item and decouple dialog from the item lifecycle.
    var selectedCard by remember { mutableStateOf<UserCard?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ⚡ Bolt Optimization: Hoist CardColors and Modifier to prevent allocation per item
    val physicalCardColors = CardDefaults.cardColors(containerColor = CardBackground)
    val virtualCardColors = CardDefaults.cardColors(containerColor = VirtualCardBackground)

    val physicalCardModifier = remember { Modifier.fillMaxWidth() }
    val virtualCardModifier = remember {
        Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = VirtualCardBorderColor,
                shape = CardShape
            )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        BackgroundGlow()

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                item(
                    key = "app_header",
                    contentType = "header"
                ) {
                    AppHeader()
                }

                item(
                    key = "section_title",
                    contentType = "title"
                ) {
                    Column {
                        SectionHeader(
                            text = "Moje karty",
                            icon = Icons.Default.CreditCard
                        )
                        Text(
                            text = "${cards.size} karty połączone",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 28.dp, bottom = 4.dp)
                        )
                    }
                }

                if (cards.isEmpty()) {
                    item(
                        key = "empty_state",
                        contentType = "empty"
                    ) {
                        CardsEmptyState()
                    }
                } else {
                    items(
                        items = cards,
                        key = { it.cardGuid },
                        contentType = { "card" }
                    ) { card ->
                        CardItemView(
                            card = card,
                            onClick = { selectedCard = card },
                            colors = if (card.isPhysical) physicalCardColors else virtualCardColors,
                            modifier = if (card.isPhysical) physicalCardModifier else virtualCardModifier
                        )
                    }
                }

                item(
                    key = "add_card_button",
                    contentType = "button"
                ) {
                    BeerWallButton(
                        text = "Dodaj nową kartę",
                        onClick = onAddCardClick,
                    )
                }

                item(
                    key = "nfc_info",
                    contentType = "info"
                ) {
                    NFCInfoCard()
                }
            }
        }
    }

    // Render sheet outside of LazyColumn
    if (selectedCard != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedCard = null },
            sheetState = sheetState,
            containerColor = CardBackground,
            contentColor = TextPrimary,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            // Wrap in safe let to smart cast
            selectedCard?.let { card ->
                CardDetailsSheetContent(
                    card = card,
                    onDismiss = { selectedCard = null },
                    onToggleStatus = {
                        onToggleCardStatus(card.cardGuid)
                        selectedCard = null
                    }
                )
            }
        }
    }
}

@Composable
fun CardItemView(
    card: UserCard,
    onClick: () -> Unit,
    colors: CardColors,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        shape = CardShape,
        colors = colors,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
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
                            VirtualCardIconBackground
                        },
                        shape = IconBoxShape
                    )
                    .then(
                        if (!card.isPhysical) {
                            Modifier.border(
                                width = 1.dp,
                                color = VirtualCardIconBorderColor,
                                shape = IconBoxShape
                            )
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = if (card.isPhysical) "Karta fizyczna" else "Karta wirtualna",
                    tint = if (card.isPhysical) DarkBackground else GoldPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = card.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (card.isPhysical) {
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

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CardDetailsSheetContent(
    card: UserCard,
    onDismiss: () -> Unit,
    onToggleStatus: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp), // Add padding for bottom navigation
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header with Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CreditCard,
                contentDescription = "Szczegóły karty",
                tint = GoldPrimary,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = card.description,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))

        // Card Type Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Typ",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Text(
                text = if (card.isPhysical) "Karta fizyczna" else "Karta wirtualna",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }

        // Status and Actions - Only for physical cards
        if (card.isPhysical) {
            // Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (card.isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (card.isActive) Success else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (card.isActive) "Aktywna" else "Nieaktywna",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = if (card.isActive) Success else TextSecondary
                    )
                }
            }

            HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))

            // Toggle Status Button
            BeerWallButton(
                text = if (card.isActive) "Zablokuj kartę" else "Odblokuj kartę",
                onClick = onToggleStatus,
                icon = if (card.isActive) Icons.Default.Block else Icons.Default.LockOpen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }

        // Close button at the bottom
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(
                text = "Zamknij",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = GoldPrimary
            )
        }
    }
}

@Preview
@Composable
fun CardDetailsSheetPhysicalPreview() {
    BeerWallTheme {
        CardDetailsSheetContent(
            card = UserCard(
                cardGuid = "1234567890",
                description = "Moja karta fizyczna",
                isActive = true,
                isPhysical = true
            ),
            onDismiss = {},
            onToggleStatus = {}
        )
    }
}

@Preview
@Composable
fun CardDetailsSheetVirtualPreview() {
    BeerWallTheme {
        CardDetailsSheetContent(
            card = UserCard(
                cardGuid = "0987654321",
                description = "Karta wirtualna",
                isActive = false,
                isPhysical = false
            ),
            onDismiss = {},
            onToggleStatus = {}
        )
    }
}

@Composable
fun NFCInfoCard() {
    BeerWallInfoCard(
        icon = Icons.Default.Nfc,
        title = "Karty NFC",
        description = "Twoje fizyczne karty są połączone z Twoim kontem. Po prostu użyj dowolnej fizycznej karty przy kranie IgiBeer, aby nalać i zapłacić.",
        iconBackground = InfoCardIconBackground
    )
}

@Composable
fun CardsEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(VirtualCardIconBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CreditCard,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nie masz jeszcze żadnych kart",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Dodaj swoją pierwszą kartę, aby zacząć korzystać z BeerWall.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun CardsScreenPreview() {
    BeerWallTheme {
        CardsScreen(
            cards = listOf(
                UserCard(
                    cardGuid = "0987654321",
                    description = "Karta wirtualna",
                    isActive = false,
                    isPhysical = false
                ),
                UserCard(
                    cardGuid = "1234567890",
                    description = "Moja karta",
                    isActive = true,
                    isPhysical = true
                )
            ),
            onAddCardClick = {},
            onToggleCardStatus = {}
        )
    }
}
