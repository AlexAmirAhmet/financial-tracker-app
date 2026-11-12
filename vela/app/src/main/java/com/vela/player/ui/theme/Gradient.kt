package com.vela.player.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Линейный градиент с углом как в CSS `linear-gradient(<angle>deg, ...)`:
 * 0° — снизу вверх, угол растёт по часовой стрелке.
 *
 * Точки start/end пересчитываются под реальный размер области: длина линии градиента
 * равна |W·sin θ| + |H·cos θ| (как в CSS), поэтому крайние цвета попадают точно в углы.
 */
class CssLinearGradient(
    private val angleDeg: Float,
    private val colors: List<Color>,
    private val stops: List<Float>,
) : ShaderBrush() {

    override fun createShader(size: Size): Shader {
        val rad = Math.toRadians(angleDeg.toDouble())
        // Направление в экранных координатах (ось Y смотрит вниз).
        val dx = sin(rad).toFloat()
        val dy = -cos(rad).toFloat()
        val half = (abs(size.width * dx) + abs(size.height * dy)) / 2f
        val cx = size.width / 2f
        val cy = size.height / 2f
        return LinearGradientShader(
            from = Offset(cx - dx * half, cy - dy * half),
            to = Offset(cx + dx * half, cy + dy * half),
            colors = colors,
            colorStops = stops,
        )
    }

    override fun equals(other: Any?): Boolean =
        other is CssLinearGradient && other.angleDeg == angleDeg && other.colors == colors && other.stops == stops

    override fun hashCode(): Int = (angleDeg.hashCode() * 31 + colors.hashCode()) * 31 + stops.hashCode()
}

/** Фон экранов: диагональный пастельный градиент 165°. */
fun VelaColors.backgroundBrush(): ShaderBrush = CssLinearGradient(GradientAngleDeg, gradient, GradientStops)

/** Горизонтальный пастельный градиент для нижней панели видеоплеера (всегда светлая палитра). */
fun playerPanelBrush(): ShaderBrush =
    CssLinearGradient(90f, LightVelaColors.gradient.map { it.copy(alpha = 0.92f) }, GradientStops)
