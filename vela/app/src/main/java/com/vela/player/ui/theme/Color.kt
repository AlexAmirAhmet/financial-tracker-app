package com.vela.player.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Палитра Vela. Значения — из задания (CLAUDE.md, раздел «Дизайн»). */
@Immutable
data class VelaColors(
    val isDark: Boolean,
    /** Цвета градиента фона; позиции — [GradientStops]. */
    val gradient: List<Color>,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val border: Color,
    val card: Color,
    val roundButton: Color,
    val formatBadge: Color,
    val accent: Color,
    val playBackground: Color,
    val playIcon: Color,
)

/** Позиции цветов градиента: 0% → 45% → 82% → 100%. */
val GradientStops = listOf(0f, 0.45f, 0.82f, 1f)

/** Угол градиента фона в CSS-смысле. */
const val GradientAngleDeg = 165f

val LightVelaColors = VelaColors(
    isDark = false,
    gradient = listOf(Color(0xFFC8F1E2), Color(0xFFF2EDF2), Color(0xFFF2B9CB), Color(0xFFE4C6E6)),
    textPrimary = Color(0xFF1E1E24),
    textSecondary = Color(0xFF3A3842),
    textTertiary = Color(0xFF55525E),
    border = Color(0xFF1E1E24),
    card = Color(110, 95, 105).copy(alpha = 0.2f),
    roundButton = Color(0xFF1E1E24).copy(alpha = 0.12f),
    formatBadge = Color(0xFFBFE0F2),
    accent = Color(0xFF8EC5E8),
    playBackground = Color(0xFF1E1E24),
    playIcon = Color.White,
)

val DarkVelaColors = VelaColors(
    isDark = true,
    gradient = listOf(Color(0xFF173430), Color(0xFF221F2A), Color(0xFF3D2433), Color(0xFF2E2340)),
    textPrimary = Color(0xFFF1EEF3),
    textSecondary = Color(0xFFD6D0DC),
    textTertiary = Color(0xFFB3ACBD),
    border = Color(0xFFE9E4EE),
    card = Color.White.copy(alpha = 0.08f),
    // В задании подложка круглых кнопок задана только для светлой темы; в тёмной — светлый цвет с той же альфой.
    roundButton = Color(0xFFF1EEF3).copy(alpha = 0.12f),
    formatBadge = Color(0xFF2F4F63),
    accent = Color(0xFF8EC5E8),
    playBackground = Color(0xFFF1EEF3),
    playIcon = Color(0xFF1E1E24),
)

/** Цвета видеоплеера (он всегда на тёмном фоне). */
object PlayerColors {
    val frame = Color.Black
    /** Светлые полупрозрачные «таблетки» панелей. */
    val pill = Color(0xFFF6F3F7).copy(alpha = 0.9f)
    val pillContent = Color(0xFF1E1E24)
}

val LocalVelaColors = staticCompositionLocalOf { LightVelaColors }
