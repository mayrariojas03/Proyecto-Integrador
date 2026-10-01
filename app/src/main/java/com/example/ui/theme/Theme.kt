package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkSmartChiclayoScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF332500),
    onPrimaryContainer = GoldAccent,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder
)

private val LightSmartChiclayoScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF3D6),
    onPrimaryContainer = Color(0xFF4A3000),
    secondary = GoldPrimary,
    onSecondary = Color.Black,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

private val PinkPastelSmartChiclayoScheme = lightColorScheme(
    primary = PastelPinkPrimary,
    onPrimary = Color.White,
    primaryContainer = PastelPinkElevated,
    onPrimaryContainer = PastelPinkTextPrimary,
    secondary = PastelPinkPrimary,
    onSecondary = Color.White,
    background = PastelPinkBackground,
    onBackground = PastelPinkTextPrimary,
    surface = PastelPinkSurface,
    onSurface = PastelPinkTextPrimary,
    surfaceVariant = PastelPinkElevated,
    onSurfaceVariant = PastelPinkTextSecondary,
    outline = PastelPinkBorder
)

private val GreenPastelSmartChiclayoScheme = lightColorScheme(
    primary = PastelGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = PastelGreenElevated,
    onPrimaryContainer = PastelGreenTextPrimary,
    secondary = PastelGreenPrimary,
    onSecondary = Color.White,
    background = PastelGreenBackground,
    onBackground = PastelGreenTextPrimary,
    surface = PastelGreenSurface,
    onSurface = PastelGreenTextPrimary,
    surfaceVariant = PastelGreenElevated,
    onSurfaceVariant = PastelGreenTextSecondary,
    outline = PastelGreenBorder
)

private val BluePastelSmartChiclayoScheme = lightColorScheme(
    primary = PastelBluePrimary,
    onPrimary = Color.White,
    primaryContainer = PastelBlueElevated,
    onPrimaryContainer = PastelBlueTextPrimary,
    secondary = PastelBluePrimary,
    onSecondary = Color.White,
    background = PastelBlueBackground,
    onBackground = PastelBlueTextPrimary,
    surface = PastelBlueSurface,
    onSurface = PastelBlueTextPrimary,
    surfaceVariant = PastelBlueElevated,
    onSurfaceVariant = PastelBlueTextSecondary,
    outline = PastelBlueBorder
)

@Composable
fun SmartChiclayoTheme(
    themeMode: AppThemeMode = AppThemeMode.OSCURO,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.OSCURO -> DarkSmartChiclayoScheme
        AppThemeMode.CLARO -> LightSmartChiclayoScheme
        AppThemeMode.ROSA_PASTEL -> PinkPastelSmartChiclayoScheme
        AppThemeMode.VERDE_PASTEL -> GreenPastelSmartChiclayoScheme
        AppThemeMode.AZUL_PASTEL -> BluePastelSmartChiclayoScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
