package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Minimal High-Contrast Monochrome Scheme (Strictly Black & White like Gemini UI)
private val MonochromeDarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    primaryContainer = DarkElevated,
    onPrimaryContainer = PureWhite,
    secondary = Gray300,
    onSecondary = PureBlack,
    secondaryContainer = DarkCard,
    onSecondaryContainer = PureWhite,
    tertiary = PureWhite,
    onTertiary = PureBlack,
    background = PureBlack,
    onBackground = PureWhite,
    surface = PureBlack,
    onSurface = PureWhite,
    surfaceVariant = DarkCard,
    onSurfaceVariant = Gray300,
    error = PureWhite,
    onError = PureBlack,
    outline = BorderDark,
    outlineVariant = BorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MonochromeDarkColorScheme,
        typography = Typography,
        content = content
    )
}
