package org.fruex.beerwall.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fruex.beerwall.ui.components.AppHeader
import org.fruex.beerwall.ui.models.DailyTransactions
import org.fruex.beerwall.ui.models.Transaction
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    transactionGroups: List<DailyTransactions>,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        if (transactionGroups.isEmpty()) {
            // Header + Empty View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
                    .padding(24.dp)
            ) {
                AppHeader()
                
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyHistoryView()
                }
            }
        } else {
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

                transactionGroups.forEach { group ->
                    item(key = "header_${group.date}") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = group.date,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldPrimary
                            )
                        }
                    }

                    items(
                        items = group.transactions,
                        key = { it.id }
                    ) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = GoldPrimary,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = null,
                    tint = DarkBackground,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.beverageName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${(if (transaction.amount < 0) "" else "+")}${transaction.amount} zł",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.amount < 0) Error else TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${transaction.volumeMilliliters}ml",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun EmptyHistoryView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "Brak transakcji",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        Text(
            text = "Twoje transakcje pojawią się tutaj",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
internal fun HistoryScreenPreview() {
    BeerWallTheme {
        HistoryScreen(
            transactionGroups = listOf(
                org.fruex.beerwall.ui.models.DailyTransactions(
                    date = "Dzisiaj, 15 Maj",
                    transactions = listOf(
                        org.fruex.beerwall.ui.models.Transaction(
                            id = "1",
                            beverageName = "Pilsner Urquell",
                            timestamp = "20:30",
                            amount = -14.50,
                            volumeMilliliters = 500
                        ),
                        org.fruex.beerwall.ui.models.Transaction(
                            id = "2",
                            beverageName = "Doładowanie",
                            timestamp = "19:15",
                            amount = 50.00,
                            volumeMilliliters = 0
                        )
                    )
                ),
                org.fruex.beerwall.ui.models.DailyTransactions(
                    date = "Wczoraj, 14 Maj",
                    transactions = listOf(
                        org.fruex.beerwall.ui.models.Transaction(
                            id = "3",
                            beverageName = "APA",
                            timestamp = "21:45",
                            amount = -16.00,
                            volumeMilliliters = 400
                        )
                    )
                )
            ),
            isRefreshing = false,
            onRefresh = {}
        )
    }
}
