package org.fruex.beerwall.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.TextSecondary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AuthHeader(
    subtitle: String,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment
    ) {
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
            subtitle = "Zaloguj do konta"
        )
    }
}
