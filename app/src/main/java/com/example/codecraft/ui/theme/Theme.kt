package com.example.codecraft.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Orange,
    secondary = Orange,
    background = DarkBlue,
    surface = SurfaceBlue,
    onPrimary = DarkBlue,
    onSecondary = DarkBlue,
    onBackground = White,
    onSurface = White
)

@Composable
fun CodeCraftTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
