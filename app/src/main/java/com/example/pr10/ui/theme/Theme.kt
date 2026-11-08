package com.example.pr10.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = OnAccent,
    background = Background,
    onBackground = TextMain,
    surface = Surface,
    onSurface = TextMain,
    onSurfaceVariant = TextMuted
)

@Composable
fun Pr10Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
