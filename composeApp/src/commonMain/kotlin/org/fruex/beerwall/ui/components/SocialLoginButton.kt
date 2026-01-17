package org.fruex.beerwall.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * Social login button with icon and centered text.
 * Supports both DrawableResource (for PNG/vector drawables) and ImageVector (for Material icons).
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
    require(iconRes != null || icon != null) { "Either iconRes or icon must be provided" }

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        border = ButtonDefaults.outlinedButtonBorder().copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.2f))
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon column - fixed width, centered
            Box(
                modifier = Modifier.width(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
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

            // Text column - centered
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Right spacer to balance icon column (32dp icon + 12dp spacer)
            Spacer(modifier = Modifier.width(44.dp))
        }
    }
}

/**
 * Convenience overload for DrawableResource icons.
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
 * Convenience overload for ImageVector icons.
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
