package com.fruex.beerwall.ui.screens.profile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.ui.components.AppHeader
import com.fruex.beerwall.ui.components.BackgroundGlow
import com.fruex.beerwall.ui.components.BeerMug
import com.fruex.beerwall.domain.model.UserProfile
import com.fruex.beerwall.ui.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Ekran profilu użytkownika.
 *
 * Wyświetla animowany kufel piwa oraz opcje ustawień.
 *
 * @param userProfile Dane profilu użytkownika (nieużywane w UI, zachowane dla kompatybilności).
 * @param tiltAngle Kąt nachylenia urządzenia.
 * @param onLogoutClick Callback wylogowania.
 * @param onChangePasswordClick Callback zmiany hasła.
 * @param onSupportClick Callback pomocy.
 * @param onAboutClick Callback informacji o aplikacji.
 */
@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    tiltAngle: Float,
    onLogoutClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onSupportClick: () -> Unit,
    onAboutClick: () -> Unit,
) {
    val fillLevel = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        fillLevel.animateTo(
            targetValue = 0.5f,
            animationSpec = tween(durationMillis = 1500)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        BackgroundGlow()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            AppHeader()

            // Profile Card (Beer Mug)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Beer Mug Animation
                    BeerMug(
                        fillLevel = fillLevel.value,
                        tiltAngle = tiltAngle,
                        modifier = Modifier.size(80.dp, 160.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Dobrze Tobie idzie, do kolejnego poziomu pozostało już tylko 7 piw.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Settings/Actions
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SettingsItem(
                        icon = Icons.Default.Settings,
                        label = "Zmiana hasła",
                        onClick = onChangePasswordClick
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = TextSecondary.copy(alpha = 0.2f)
                    )

                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.Help,
                        label = "Pomoc i wsparcie",
                        onClick = onSupportClick
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = TextSecondary.copy(alpha = 0.2f)
                    )

                    SettingsItem(
                        icon = Icons.Default.Info,
                        label = "O aplikacji",
                        onClick = onAboutClick
                    )
                }
            }

            // Logout BeerWallButton
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                ),
                onClick = onLogoutClick
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = Error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Wyloguj",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Error
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        color = CardBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    BeerWallTheme {
        ProfileScreen(
            userProfile = UserProfile(
                name = "Jan Kowalski"
            ),
            tiltAngle = 0f,
            onLogoutClick = {},
            onChangePasswordClick = {},
            onSupportClick = {},
            onAboutClick = {}
        )
    }
}
