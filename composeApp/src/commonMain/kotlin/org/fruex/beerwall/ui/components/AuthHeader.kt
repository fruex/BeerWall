package org.fruex.beerwall.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.GoldPrimary
import org.fruex.beerwall.ui.theme.TextSecondary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AuthHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = GoldPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
    }
}

@Preview
@Composable
fun AuthHeaderPreview() {
    BeerWallTheme {
        AuthHeader(
            title = "Igi Beer System",
            subtitle = "Zaloguj do konta"
        )
    }
}
