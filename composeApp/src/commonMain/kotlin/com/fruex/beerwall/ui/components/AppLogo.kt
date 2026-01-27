package com.fruex.beerwall.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import beerwall.composeapp.generated.resources.Res
import beerwall.composeapp.generated.resources.logo
import com.fruex.beerwall.ui.theme.BeerWallTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.logo),
        contentDescription = "IGI BEER",
        modifier = modifier.height(80.dp),
        contentScale = ContentScale.Fit
    )
}

@Preview
@Composable
fun AppLogoPreview() {
    BeerWallTheme {
        AppLogo()
    }
}
