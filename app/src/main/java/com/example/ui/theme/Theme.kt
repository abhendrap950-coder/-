package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Ancient / Tadpatra Mode Scheme
private val TadpatraColorScheme = lightColorScheme(
    primary = GitaCopper,
    onPrimary = Color.White,
    primaryContainer = GitaParchmentSurfaceVariant,
    onPrimaryContainer = GitaParchmentText,
    secondary = GitaGoldWarm,
    onSecondary = Color.Black,
    secondaryContainer = GitaParchmentSurface,
    onSecondaryContainer = GitaParchmentText,
    background = GitaParchmentBg,
    onBackground = GitaParchmentText,
    surface = GitaParchmentSurface,
    onSurface = GitaParchmentText,
    surfaceVariant = GitaParchmentSurfaceVariant,
    onSurfaceVariant = GitaParchmentSubText,
    outline = GitaParchmentBorder
)

// Dark Temple / Vedic Cave Scheme
private val DarkTempleColorScheme = darkColorScheme(
    primary = GitaDeepGold,
    onPrimary = Color.Black,
    primaryContainer = GitaDarkSurfaceVariant,
    onPrimaryContainer = GitaGoldBright,
    secondary = GitaGoldWarm,
    onSecondary = Color.Black,
    secondaryContainer = GitaDarkSurface,
    onSecondaryContainer = GitaTextPrimary,
    background = GitaDarkBg,
    onBackground = GitaTextPrimary,
    surface = GitaDarkSurface,
    onSurface = GitaTextPrimary,
    surfaceVariant = GitaDarkSurfaceVariant,
    onSurfaceVariant = GitaTextSecondary,
    outline = GitaDarkBorder
)

@Composable
fun BhagavadGitaTheme(
    appThemeMode: String = "ancient", // "dark", "light", "ancient"
    content: @Composable () -> Unit
) {
    val colorScheme = when (appThemeMode) {
        "dark" -> DarkTempleColorScheme
        "light" -> TadpatraColorScheme
        else -> DarkTempleColorScheme // Default ancient Vedic stone / dark gold
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
