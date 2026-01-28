package com.fruex.beerwall.ui.screens.balance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.fruex.beerwall.ui.components.BeerWallInfoCard
import com.fruex.beerwall.ui.components.SectionHeader
import com.fruex.beerwall.ui.models.PremisesBalance
import com.fruex.beerwall.ui.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview

private val IconBackgroundOverlay = DarkBackground.copy(alpha = 0.2f)

/**
 * Ekran salda.
 *
 * Wyświetla dostępne środki w różnych lokalach oraz punkty lojalnościowe.
 *
 * @param balances Lista sald.
 * @param isRefreshing Flaga odświeżania.
 * @param onRefresh Callback odświeżania.
 * @param onAddFundsClick Callback do ekranu doładowania.
 * @param onAddLocationClick Callback dodawania lokalizacji (nieużywany w UI, ale dostępny).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    balances: List<PremisesBalance>,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onAddFundsClick: (premisesId: Int) -> Unit,
    onAddLocationClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    // ⚡ Bolt Optimization: Hoist CardColors to prevent allocation per item in LazyColumn
    val balanceCardColors = CardDefaults.cardColors(
        containerColor = GoldPrimary
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

                item(
                    key = "section_title",
                    contentType = "title"
                ) {
                    SectionHeader(
                        text = "Dostępne saldo",
                        icon = Icons.Default.AccountBalanceWallet
                    )
                }

                items(
                    items = balances,
                    key = { it.premisesId },
                    contentType = { "balance_card" }
                ) { premisesBalance ->
                    BalanceCard(
                        premisesName = premisesBalance.premisesName,
                        formattedBalance = premisesBalance.formattedBalance,
                        formattedLoyaltyPoints = premisesBalance.formattedLoyaltyPoints,
                        onAddFundsClick = { onAddFundsClick(premisesBalance.premisesId) },
                        colors = balanceCardColors
                    )
                }

                item(
                    key = "info_card",
                    contentType = "info"
                ) {
                    InfoCard()
                }
            }
        }
    }
}

@Composable
fun BalanceCard(
    premisesName: String,
    formattedBalance: String,
    formattedLoyaltyPoints: String,
    onAddFundsClick: () -> Unit,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = GoldPrimary
    )
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = colors
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top Right Button
            IconButton(
                onClick = onAddFundsClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(48.dp)
                    .background(
                        color = IconBackgroundOverlay,
                        shape = IconBoxShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dodaj środki",
                    tint = DarkBackground,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Premises Name
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = premisesName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkBackground
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Balance and Points in one line
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Balance
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formattedBalance,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground
                        )
                    }

                    // Points
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedLoyaltyPoints,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard() {
    BeerWallInfoCard(
        icon = Icons.Default.Info,
        title = "Jak to działa",
        description = "Dodaj środki do salda i używaj podłączonych kart NFC przy każdym kranie IgiBeer. Saldo zostanie automatycznie odliczone podczas nalewania."
    )
}

@Preview
@Composable
fun BalanceScreenPreview() {
    BeerWallTheme {
        BalanceScreen(
            balances = listOf(
                PremisesBalance(
                    premisesId = 1,
                    premisesName = "Pub Centrum",
                    balance = 45.50,
                    loyaltyPoints = 120,
                    formattedBalance = "45.50 zł",
                    formattedLoyaltyPoints = "120 pkt"
                ),
                PremisesBalance(
                    premisesId = 2,
                    premisesName = "Bar przy Rynku",
                    balance = 12.00,
                    loyaltyPoints = 50,
                    formattedBalance = "12.00 zł",
                    formattedLoyaltyPoints = "50 pkt"
                )
            ),
            onAddFundsClick = {},
            onAddLocationClick = {}
        )
    }
}
