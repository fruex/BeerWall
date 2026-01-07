package org.fruex.beerwall.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.CardBackground
import org.fruex.beerwall.ui.theme.DarkBackground
import org.fruex.beerwall.ui.theme.TextSecondary
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "O aplikacji",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wróć"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Beer Wall",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Wersja 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Beer Wall to innowacyjny system samoobsługowy umożliwiający nalewanie piwa z wykorzystaniem technologii NFC i zarządzania saldem online.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Funkcje aplikacji:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "• Zarządzanie saldem online\n" +
                                "• Szybkie płatności BLIK\n" +
                                "• Program lojalnościowy\n" +
                                "• Integracja z kartami NFC\n" +
                                "• Historia transakcji\n" +
                                "• Wielolokalizacyjność",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "© 2026 Beer Wall. Wszystkie prawa zastrzeżone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Text(
                        text = "Aplikacja stworzona z wykorzystaniem Kotlin Multiplatform i Jetpack Compose.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    BeerWallTheme {
        AboutScreen(
            onBackClick = {}
        )
    }
}
