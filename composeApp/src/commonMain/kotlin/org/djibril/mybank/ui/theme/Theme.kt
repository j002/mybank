package org.djibril.mybank.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CaGreen,
    onPrimary = Color.White,

    surface = CardBackground,
    background = SectionBackground,

    surfaceVariant = CaGreenLight,
    onSurfaceVariant = TextSecondary,

    outlineVariant = CaGreenSoft
)

@Composable
fun MyBankTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
