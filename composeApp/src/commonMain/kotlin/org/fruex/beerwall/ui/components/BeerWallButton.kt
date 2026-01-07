package org.fruex.beerwall.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.fruex.beerwall.ui.theme.BeerWallTheme
import org.fruex.beerwall.ui.theme.DarkBackground
import org.fruex.beerwall.ui.theme.GoldDark
import org.fruex.beerwall.ui.theme.GoldPrimary
import org.fruex.beerwall.ui.theme.InputBackground
import org.fruex.beerwall.ui.theme.TextPrimary
import org.fruex.beerwall.ui.theme.TextSecondary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BeerWallButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = GoldDark,
            contentColor = TextSecondary,
            disabledContainerColor = GoldDark.copy(alpha = 0.5f),
            disabledContentColor = TextSecondary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BeerWallOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = InputBackground,
            contentColor = TextPrimary,
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.2f))
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified // To keep original colors if icon is multi-color
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
fun BeerWallButtonPreview() {
    BeerWallTheme {
        BeerWallButton(
            text = "Zaloguj",
            onClick = {}
        )
    }
}
