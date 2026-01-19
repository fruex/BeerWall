package com.fruex.beerwall.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.ui.theme.BeerWallTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        AppLogo()
    }
}

@Preview
@Composable
fun AppHeaderPreview() {
    BeerWallTheme {
        AppHeader()
    }
}
