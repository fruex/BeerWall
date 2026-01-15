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
import org.fruex.beerwall.ui.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Ekran historii transakcji.
 *
 * Wyświetla listę transakcji pogrupowanych według daty.
 *
 * @param transactionGroups Lista grup transakcji.
 * @param isRefreshing Flaga odświeżania.
 * @param onRefresh Callback odświeżania.
 */
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
                        key = { it.transactionId }
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
                    text = transaction.commodityName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.startDateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${(if (transaction.grossPrice < 0) "" else "+")}${transaction.grossPrice} zł",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.grossPrice < 0) Error else TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${transaction.capacity}ml",
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

@Preview
@Composable
fun HistoryScreenPreview() {
    BeerWallTheme {
        HistoryScreen(
            transactionGroups = listOf(
                DailyTransactions(
                    date = "Dzisiaj",
                    transactions = listOf(
                        Transaction(
                            transactionId = 1,
                            commodityName = "Piwo Jasne",
                            grossPrice = -12.50,
                            capacity = 500,
                            startDateTime = "2024-11-24T18:30:00"
                        ),
                        Transaction(
                            transactionId = 2,
                            commodityName = "Doładowanie",
                            grossPrice = 50.00,
                            capacity = 0,
                            startDateTime = "2024-11-24T18:00:00"
                        )
                    )
                ),
                DailyTransactions(
                    date = "Wczoraj",
                    transactions = listOf(
                        Transaction(
                            transactionId = 3,
                            commodityName = "Piwo Ciemne",
                            grossPrice = -15.00,
                            capacity = 500,
                            startDateTime = "2024-11-23T20:15:00"
                        )
                    )
                )
            )
        )
    }
}
