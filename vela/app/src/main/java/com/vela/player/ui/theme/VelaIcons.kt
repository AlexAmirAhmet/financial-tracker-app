package com.vela.player.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Собственные тонкие линейные иконки Vela (сетка 24×24, линия 1.5).
 * Цвет задаётся через tint у Icon, поэтому здесь рисуем чёрным.
 */
object VelaIcons {
    val Search = line("Search", circle(11f, 11f, 7f), "M16.2,16.2L20.5,20.5")
    val Plus = line("Plus", "M12,5V19", "M5,12H19")
    val Close = line("Close", "M6.5,6.5L17.5,17.5", "M17.5,6.5L6.5,17.5")
    val ArrowRight = line("ArrowRight", "M5,12H19", "M13.5,6.5L19,12L13.5,17.5")
    val Back = line("Back", "M19,12H5", "M10.5,6.5L5,12L10.5,17.5")
    val ChevronRight = line("ChevronRight", "M9.5,6L15.5,12L9.5,18")
    val Folder = line("Folder", "M3.5,7.5C3.5,6.4 4.4,5.5 5.5,5.5H9.3L11.3,7.7H18.5C19.6,7.7 20.5,8.6 20.5,9.7V17C20.5,18.1 19.6,19 18.5,19H5.5C4.4,19 3.5,18.1 3.5,17Z")
    val Video = line("Video", "M5,5.5H19C20.1,5.5 21,6.4 21,7.5V16.5C21,17.6 20.1,18.5 19,18.5H5C3.9,18.5 3,17.6 3,16.5V7.5C3,6.4 3.9,5.5 5,5.5Z", "M10.3,9.3L14.8,12L10.3,14.7Z")
    val Music = line("Music", "M9,17.5V6.2L19.5,4.2V15.5", circle(6.6f, 17.5f, 2.4f), circle(17.1f, 15.5f, 2.4f))
    val Settings = line("Settings", "M4,7H13", "M18.5,7H20", circle(15.8f, 7f, 2.3f), "M4,17H5.5", "M11,17H20", circle(8.2f, 17f, 2.3f))
    val Stream = line("Stream", circle(12f, 12f, 9f), "M3,12H21", "M12,3C9,6 9,18 12,21", "M12,3C15,6 15,18 12,21")
    val Previous = line("Previous", "M18,6.5L9.5,12L18,17.5Z", "M6,6V18")
    val Next = line("Next", "M6,6.5L14.5,12L6,17.5Z", "M18,6V18")
    val Shuffle = line(
        "Shuffle",
        "M3.5,7H6.5C10.5,7 12.5,17 16.5,17H20", "M3.5,17H6.5C8,17 9,15.8 10,14.3", "M20,7H16.5C15,7 14,8.2 13,9.7",
        "M18,5L20,7L18,9", "M18,15L20,17L18,19",
    )
    val Repeat = line("Repeat", "M4,11.5V9.5C4,8.4 4.9,7.5 6,7.5H19", "M16.5,5L19,7.5L16.5,10", "M20,12.5V14.5C20,15.6 19.1,16.5 18,16.5H5", "M7.5,19L5,16.5L7.5,14")
    val Speed = line("Speed", "M4.6,17.5A8.5,8.5 0,1 1,19.4 17.5", "M12,13.5L15.8,9.2", circle(12f, 13.5f, 0.8f))
    val Timer = line("Timer", "M19.5,14.6A8,8 0,1 1,9.4 4.5A6.5,6.5 0,0 0,19.5 14.6Z")
    val Equalizer = line("Equalizer", "M6,4V20", "M12,4V20", "M18,4V20", "M4,15H8", "M10,8.5H14", "M16,13H20")
    val Queue = line("Queue", "M4,6.5H20", "M4,12H20", "M4,17.5H13", "M16.5,15.5V20.5", "M14,18H19")
    val Lock = line("Lock", "M6.5,10.5H17.5C18.3,10.5 19,11.2 19,12V18.5C19,19.3 18.3,20 17.5,20H6.5C5.7,20 5,19.3 5,18.5V12C5,11.2 5.7,10.5 6.5,10.5Z", "M8.5,10.5V7.5A3.5,3.5 0,0 1,15.5 7.5V10.5")

    /** Залитый треугольник Play (для главной кнопки). */
    val Play = filled("Play", "M8.5,5.8C8.5,5 9.4,4.5 10.1,4.9L19,10.7C19.6,11.1 19.6,12.9 19,13.3L10.1,19.1C9.4,19.5 8.5,19 8.5,18.2Z")
    val Pause = filled("Pause", "M7,5.5H10V18.5H7Z", "M14,5.5H17V18.5H14Z")
}

private fun circle(cx: Float, cy: Float, r: Float): String =
    "M${cx - r},${cy}A$r,$r 0,1 0,${cx + r},${cy}A$r,$r 0,1 0,${cx - r},${cy}Z"

private fun line(name: String, vararg paths: String): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
        paths.forEach {
            addPath(
                pathData = addPathNodes(it),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()

private fun filled(name: String, vararg paths: String): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
        paths.forEach {
            addPath(
                pathData = addPathNodes(it),
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1f,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()
