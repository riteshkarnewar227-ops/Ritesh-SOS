package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Life360Purple,
    onPrimary = Color.White,
    primaryContainer = Life360Indigo,
    onPrimaryContainer = Color.White,
    secondary = Life360Green,
    onSecondary = Color.Black,
    secondaryContainer = Life360DarkSurfaceElevated,
    onSecondaryContainer = Color.White,
    tertiary = Life360Amber,
    onTertiary = Color.Black,
    background = Life360DarkBg,
    onBackground = Life360TextPrimary,
    surface = Life360DarkSurface,
    onSurface = Life360TextPrimary,
    surfaceVariant = Life360DarkSurfaceElevated,
    onSurfaceVariant = Life360TextSecondary,
    outline = Life360DarkBorder,
    error = Life360Red,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Life360Purple,
    onPrimary = Color.White,
    primaryContainer = Life360PurpleBg,
    onPrimaryContainer = Life360PurpleDark,
    secondary = Life360Green,
    onSecondary = Color.White,
    secondaryContainer = Life360GreenBg,
    onSecondaryContainer = Life360GreenDark,
    tertiary = Life360Amber,
    onTertiary = Color.White,
    background = Life360LightBg,
    onBackground = Life360TextPrimary,
    surface = Life360LightSurface,
    onSurface = Life360TextPrimary,
    surfaceVariant = Life360LightSurfaceElevated,
    onSurfaceVariant = Life360TextSecondary,
    outline = Life360LightBorder,
    error = Life360Red,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

