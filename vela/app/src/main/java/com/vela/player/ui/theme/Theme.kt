package com.vela.player.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vela.player.settings.ThemeMode

/** Размеры и скругления из задания. */
object VelaDimens {
    val screenPadding = 24.dp
    val borderWidth = 1.5.dp
    val blockRadius = 24.dp
    val cardRadius = 20.dp
    val minTouch = 44.dp
    val roundButton = 48.dp
    val playButton = 76.dp
}

private val VelaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(VelaDimens.cardRadius),
    large = RoundedCornerShape(VelaDimens.blockRadius),
)

@Composable
fun isDarkTheme(mode: ThemeMode): Boolean = when (mode) {
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
}

@Composable
fun VelaTheme(dark: Boolean, content: @Composable () -> Unit) {
    val colors = if (dark) DarkVelaColors else LightVelaColors
    val scheme = if (dark) {
        darkColorScheme(
            primary = colors.playBackground, onPrimary = colors.playIcon,
            secondary = colors.accent, onSecondary = Color(0xFF1E1E24),
            background = colors.gradient[1], onBackground = colors.textPrimary,
            surface = Color(0xFF2A2633), onSurface = colors.textPrimary,
            surfaceVariant = Color(0xFF332E3B), onSurfaceVariant = colors.textSecondary,
            outline = colors.border,
        )
    } else {
        lightColorScheme(
            primary = colors.playBackground, onPrimary = colors.playIcon,
            secondary = colors.accent, onSecondary = Color(0xFF1E1E24),
            background = colors.gradient[1], onBackground = colors.textPrimary,
            surface = Color(0xFFF7F3F7), onSurface = colors.textPrimary,
            surfaceVariant = Color(0xFFEDE6EC), onSurfaceVariant = colors.textSecondary,
            outline = colors.border,
        )
    }
    CompositionLocalProvider(LocalVelaColors provides colors) {
        MaterialTheme(colorScheme = scheme, typography = VelaTypography, shapes = VelaShapes, content = content)
    }
}

/** Короткий доступ к палитре: `Vela.colors`. */
object Vela {
    val colors: VelaColors
        @Composable @ReadOnlyComposable get() = LocalVelaColors.current
}
