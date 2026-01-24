package com.fruex.beerwall.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.fruex.beerwall.ui.components.AppHeader
import com.fruex.beerwall.ui.models.DailyTransactions
import com.fruex.beerwall.ui.models.Transaction
import com.fruex.beerwall.ui.theme.*
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
                item(
                    key = "app_header",
                    contentType = "header"
                ) {
                    AppHeader()
                }

                transactionGroups.forEach { group ->
                    item(
                        key = "header_${group.date}",
                        contentType = "group_header"
                    ) {
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
                        key = { it.transactionId },
                        contentType = { "transaction" }
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
        shape = CardShape,
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
                        shape = IconBoxShape
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
                    text = "${transaction.premisesName} o ${transaction.formattedTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${transaction.grossPrice} zł",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${transaction.capacity} ml",
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
                            formattedTime = "18:30",
                            premisesName = "Pub Warszawski"
                        ),
                        Transaction(
                            transactionId = 2,
                            commodityName = "Piwo ciemne",
                            grossPrice = -12.50,
                            capacity = 330,
                            formattedTime = "18:00",
                            premisesName = "Pub Warszawski"
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
                            formattedTime = "20:15",
                            premisesName = "Pub Krakowski"
                        )
                    )
                )
            )
        )
    }
}
