package com.example.movierater.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandDarkGreen,
    onPrimary = Color.White,
    secondary = BrandMagenta,
    onSecondary = Color.White,
    tertiary = BrandSageGreen,
    background = SurfaceDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = BrandDarkGreen,
    onPrimary = Color.White,
    secondary = BrandMagenta,
    onSecondary = Color.White,
    tertiary = BrandSageGreen,
    background = Color(0xFFF8F5F1),
    onBackground = Color(0xFF1B1B1B),
    surface = SurfaceCard,
    onSurface = Color(0xFF1B1B1B),
)

@Composable
fun MovieRaterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
