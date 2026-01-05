package org.fruex.beerwall.ui.screens.balance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fruex.beerwall.ui.components.AppHeader
import org.fruex.beerwall.ui.components.BeerWallButton
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
    onAddFundsClick: (venueName: String) -> Unit,
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
                key = { it.venueName }
            ) { venueBalance ->
                BalanceCard(
                    venueName = venueBalance.venueName,
                    balance = venueBalance.balance,
                    onAddFundsClick = { onAddFundsClick(venueBalance.venueName) }
                )
            }

            item(key = "add_funds_button") {
                BeerWallButton(
                    text = "Dodaj środki",
                    onClick = onAddLocationClick,
                )
            }

            item(key = "info_card") {
                Spacer(modifier = Modifier.height(12.dp))
                InfoCard()
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun BalanceCard(
    venueName: String,
    balance: Double,
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${balance} zł",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkBackground
                )
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
                    imageVector = Icons.Default.Wallet,
                    contentDescription = "Dodaj środki",
                    tint = DarkBackground
                )
            }
        }
    }
}

@Composable
fun InfoCard() {
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
                    text = "Jak to działa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Dodaj środki do salda i używaj podłączonych kart NFC przy każdym kranie Beer Wall. Saldo zostanie automatycznie odliczone podczas nalewania.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
