package org.fruex.beerwall.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.GoldPrimary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dot pattern
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(8.dp).background(GoldPrimary, CircleShape))
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Box(modifier = Modifier.size(8.dp).background(GoldPrimary, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(8.dp).background(GoldPrimary, CircleShape))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.size(8.dp).background(GoldPrimary, CircleShape))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "IGI BEER",
            style = MaterialTheme.typography.displaySmall,
            color = GoldPrimary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        )
    }
}

@Preview
@Composable
fun AppLogoPreview() {
    BeerWallTheme {
        AppLogo()
    }
}
