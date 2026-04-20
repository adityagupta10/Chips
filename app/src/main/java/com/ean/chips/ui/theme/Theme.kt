package com.ean.chips.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val GamifiedColorScheme = darkColorScheme(
    primary = PrimaryNeon,
    secondary = AccentGold,
    tertiary = SuccessGreen,
    background = DarkBackground,
    surface = SurfaceCard,
    surfaceVariant = SurfaceCardElevated,
    onPrimary = TextPrimary,
    onSecondary = DarkBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

@Composable
fun ChipsTheme(
    content: @Composable () -> Unit
) {
    // We enforce the custom gamified theme
    MaterialTheme(
        colorScheme = GamifiedColorScheme,
        typography = Typography,
        content = content
    )
}