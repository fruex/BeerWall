package org.fruex.beerwall.ui.screens.balance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fruex.beerwall.ui.components.*
import org.fruex.beerwall.ui.models.VenueBalance
import org.fruex.beerwall.ui.theme.CardBackground
import org.fruex.beerwall.ui.theme.DarkBackground
import org.fruex.beerwall.ui.theme.GoldPrimary
import org.fruex.beerwall.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    balances: List<VenueBalance>,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onAddFundsClick: (venueId: Int) -> Unit,
    onAddLocationClick: () -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item(key = "app_header") {
                AppHeader()
            }

            item(key = "section_title") {
                Text(
                    text = "Dostępne saldo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(
                items = balances,
                key = { it.venueId }
            ) { venueBalance ->
                BalanceCard(
                    venueName = venueBalance.venueName,
                    balance = venueBalance.balance,
                    loyaltyPoints = venueBalance.loyaltyPoints,
                    onAddFundsClick = { onAddFundsClick(venueBalance.venueId) }
                )
            }

            item(key = "info_card") {
                Spacer(modifier = Modifier.height(12.dp))
                InfoCard()
            }
        }
    }
}

@Composable
fun BalanceCard(
    venueName: String,
    balance: Double,
    loyaltyPoints: Int,
    onAddFundsClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GoldPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = venueName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkBackground
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$loyaltyPoints pkt",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💰",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${balance} zł",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground
                        )
                    }
                }
            }

            IconButton(
                onClick = onAddFundsClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = DarkBackground.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dodaj środki",
                    tint = DarkBackground,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun InfoCard() {
    BeerWallInfoCard(
        icon = Icons.Default.Info,
        title = "Jak to działa",
        description = "Dodaj środki do salda i używaj podłączonych kart NFC przy każdym kranie Beer Wall. Saldo zostanie automatycznie odliczone podczas nalewania."
    )
}
