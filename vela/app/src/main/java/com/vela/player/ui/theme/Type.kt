package com.vela.player.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vela.player.R

/** Montserrat 400/500/600/700 — весь обычный текст. */
val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
)

/** Caveat Bold — рукописные заголовки. */
val Caveat = FontFamily(Font(R.font.caveat_bold, FontWeight.Bold))

object VelaText {
    /** Большой рукописный заголовок экрана («привет», «сейчас играет»). */
    val handwrittenLarge = TextStyle(fontFamily = Caveat, fontWeight = FontWeight.Bold, fontSize = 52.sp, lineHeight = 56.sp)
    /** Рукописный заголовок панелей. */
    val handwrittenMedium = TextStyle(fontFamily = Caveat, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 40.sp)
    /** Названия файлов, пунктов. */
    val title = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 19.sp)
    val body = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp)
    /** Подписи: длительность, размер, даты. */
    val caption = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 15.sp)
    /** Плашка формата (MP4, FLAC). */
    val badge = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Bold, fontSize = 10.sp, lineHeight = 12.sp, letterSpacing = 0.6.sp)
    val chip = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp)
}

private val base = Typography()

/** Типографика Material 3, переведённая на Montserrat. */
val VelaTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Montserrat),
    displayMedium = base.displayMedium.copy(fontFamily = Montserrat),
    displaySmall = base.displaySmall.copy(fontFamily = Montserrat),
    headlineLarge = base.headlineLarge.copy(fontFamily = Montserrat),
    headlineMedium = base.headlineMedium.copy(fontFamily = Montserrat),
    headlineSmall = base.headlineSmall.copy(fontFamily = Montserrat),
    titleLarge = base.titleLarge.copy(fontFamily = Montserrat, fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontFamily = Montserrat, fontWeight = FontWeight.SemiBold),
    titleSmall = base.titleSmall.copy(fontFamily = Montserrat, fontWeight = FontWeight.SemiBold),
    bodyLarge = base.bodyLarge.copy(fontFamily = Montserrat, fontSize = 14.sp),
    bodyMedium = base.bodyMedium.copy(fontFamily = Montserrat, fontSize = 13.sp),
    bodySmall = base.bodySmall.copy(fontFamily = Montserrat, fontSize = 11.sp),
    labelLarge = base.labelLarge.copy(fontFamily = Montserrat, fontWeight = FontWeight.SemiBold),
    labelMedium = base.labelMedium.copy(fontFamily = Montserrat, fontWeight = FontWeight.Medium),
    labelSmall = base.labelSmall.copy(fontFamily = Montserrat, fontWeight = FontWeight.Medium),
)
