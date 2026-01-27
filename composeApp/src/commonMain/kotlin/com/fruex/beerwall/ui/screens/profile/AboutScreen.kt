package com.fruex.beerwall.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.BuildKonfig
import com.fruex.beerwall.ui.theme.BeerWallTheme
import com.fruex.beerwall.ui.theme.TextSecondary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AboutScreen(
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "O aplikacji",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))

        Text(
            text = "IgiBeer",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Wersja ${BuildKonfig.APP_VERSION}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(0.dp))

        Text(
            text = "IgiBeer to innowacyjny system samoobsługowy umożliwiający nalewanie piwa z wykorzystaniem technologii NFC i zarządzania saldem online.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(0.dp))

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

        Spacer(modifier = Modifier.height(0.dp))

        HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))

        Spacer(modifier = Modifier.height(0.dp))

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

@Preview
@Composable
fun AboutScreenPreview() {
    BeerWallTheme {
        AboutScreen(
            onDismiss = {}
        )
    }
}
