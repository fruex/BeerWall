package com.fruex.beerwall.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import beerwall.composeapp.generated.resources.Res
import beerwall.composeapp.generated.resources.ic_facebook
import beerwall.composeapp.generated.resources.ic_google
import com.fruex.beerwall.ui.theme.BeerWallTheme
import com.fruex.beerwall.ui.theme.GoldPrimary
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Przycisk logowania społecznościowego z ikoną i wyśrodkowanym tekstem.
 * Obsługuje zarówno DrawableResource (dla PNG/wektorów) jak i ImageVector (dla ikon Material).
 */
@Composable
fun SocialLoginButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    iconRes: DrawableResource? = null,
    icon: ImageVector? = null
) {
    require(iconRes != null || icon != null) { "Należy podać iconRes lub icon" }

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = GoldPrimary
        ),
        border = ButtonDefaults.outlinedButtonBorder().copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kolumna ikony - stała szerokość, wyśrodkowana
            Box(
                modifier = Modifier.width(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (iconRes != null) {
                    // Białe okrągłe tło dla ikon społecznościowych
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = Color.White
                    ) {
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(3.dp),
                            tint = Color.Unspecified
                        )
                    }
                } else if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Kolumna tekstu - wyśrodkowana
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Prawy odstęp dla zrównoważenia kolumny ikony (32dp ikona + 12dp odstęp)
            Spacer(modifier = Modifier.width(44.dp))
        }
    }
}

/**
 * Wygodne przeciążenie dla ikon DrawableResource.
 */
@Composable
fun SocialLoginButton(
    text: String,
    onClick: () -> Unit,
    iconRes: DrawableResource,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    SocialLoginButton(
        text = text,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        iconRes = iconRes,
        icon = null
    )
}

/**
 * Wygodne przeciążenie dla ikon ImageVector.
 */
@Composable
fun SocialLoginButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    SocialLoginButton(
        text = text,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        iconRes = null,
        icon = icon
    )
}

@Preview
@Composable
fun SocialLoginButtonPreview() {
    BeerWallTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SocialLoginButton(
                text = "Kontynuuj z Google",
                onClick = {},
                iconRes = Res.drawable.ic_google,
                enabled = true
            )
            SocialLoginButton(
                text = "Kontynuuj z Facebook",
                onClick = {},
                iconRes = Res.drawable.ic_facebook,
                enabled = false
            )
        }
    }
}
