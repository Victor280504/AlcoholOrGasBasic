package com.example.alcoholorgas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = LightTealPrimary,
    onPrimary = SurfaceLight,

    secondary = LightBlueSecondary,
    onSecondary = SurfaceLight,

    background = BackgroundLight,
    onBackground = TextPrimary,

    surface = SurfaceLight,
    onSurface = TextPrimary,
    onSurfaceVariant = LightTextSecondary,

    tertiary = OrangeAccent,
    primaryContainer = Color.White,
    errorContainer = PinkAccent
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = TextPrimary,

    secondary = DarkBlueSecondary,
    onSecondary = DarkTextPrimary,

    background = DarkBackground,
    onBackground = DarkTextPrimary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,

    tertiary = OrangeAccent,
    primaryContainer = Color.Black,
    errorContainer = PinkAccent
)

@Composable
fun AlcoholOrGasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}