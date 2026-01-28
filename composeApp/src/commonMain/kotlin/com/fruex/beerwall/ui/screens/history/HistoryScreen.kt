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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.ui.components.AppHeader
import com.fruex.beerwall.ui.components.BackgroundGlow
import com.fruex.beerwall.ui.components.SectionHeader
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
    onRefresh: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    // ⚡ Bolt Optimization: Hoist CardColors to prevent allocation per item in LazyColumn
    val transactionCardColors = CardDefaults.cardColors(
        containerColor = CardBackground
    )

    val layoutDirection = LocalLayoutDirection.current
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    val mergedPadding = PaddingValues(
        start = 24.dp + contentPadding.calculateStartPadding(layoutDirection) + systemBarsPadding.calculateStartPadding(layoutDirection),
        top = 24.dp + contentPadding.calculateTopPadding() + systemBarsPadding.calculateTopPadding(),
        end = 24.dp + contentPadding.calculateEndPadding(layoutDirection) + systemBarsPadding.calculateEndPadding(layoutDirection),
        bottom = 24.dp + contentPadding.calculateBottomPadding() + systemBarsPadding.calculateBottomPadding()
    )

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
            if (transactionGroups.isEmpty()) {
                // Header + Empty View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = mergedPadding.calculateStartPadding(layoutDirection),
                            top = mergedPadding.calculateTopPadding(),
                            end = mergedPadding.calculateEndPadding(layoutDirection),
                            bottom = mergedPadding.calculateBottomPadding()
                        )
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
                        .fillMaxSize(),
                    contentPadding = mergedPadding,
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
                            SectionHeader(
                                text = group.date,
                                icon = Icons.Default.CalendarToday
                            )
                        }

                        items(
                            items = group.transactions,
                            key = { it.transactionId },
                            contentType = { "transaction" }
                        ) { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                colors = transactionCardColors
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = CardBackground
    )
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = colors
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
                    text = transaction.formattedDetails,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = transaction.formattedPrice,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.formattedCapacity,
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
                            formattedPrice = "-12.50 zł",
                            formattedCapacity = "500 ml",
                            formattedDetails = "Pub Warszawski o 18:30"
                        ),
                        Transaction(
                            transactionId = 2,
                            commodityName = "Piwo ciemne",
                            formattedPrice = "-12.50 zł",
                            formattedCapacity = "330 ml",
                            formattedDetails = "Pub Warszawski o 18:00"
                        )
                    )
                ),
                DailyTransactions(
                    date = "Wczoraj",
                    transactions = listOf(
                        Transaction(
                            transactionId = 3,
                            commodityName = "Piwo Ciemne",
                            formattedPrice = "-15.00 zł",
                            formattedCapacity = "500 ml",
                            formattedDetails = "Pub Krakowski o 20:15"
                        )
                    )
                )
            )
        )
    }
}
