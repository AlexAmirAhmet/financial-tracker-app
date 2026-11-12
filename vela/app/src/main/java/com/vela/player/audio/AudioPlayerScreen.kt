package com.vela.player.audio

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vela.player.R
import com.vela.player.ui.theme.BorderedBlock
import com.vela.player.ui.theme.FormatBadge
import com.vela.player.ui.theme.PlayButton
import com.vela.player.ui.theme.ProgressLine
import com.vela.player.ui.theme.RoundIconButton
import com.vela.player.ui.theme.ScreenHeader
import com.vela.player.ui.theme.Vela
import com.vela.player.ui.theme.VelaCard
import com.vela.player.ui.theme.VelaIcons
import com.vela.player.ui.theme.VelaScreen
import com.vela.player.ui.theme.VelaText
import kotlin.math.PI
import kotlin.math.sin

/** Аудиоплеер. Этап 1 — макет; реальное воспроизведение появится на этапе 6. */
@Composable
fun AudioPlayerScreen(onBack: () -> Unit) {
    val colors = Vela.colors
    var playing by rememberSaveable { mutableStateOf(false) }

    VelaScreen {
        ScreenHeader(stringResource(R.string.audio_now_playing), onBack, stringResource(R.string.action_back))

        BorderedBlock {
            DecorativeBars(playing, Modifier.fillMaxWidth().height(160.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.audio_nothing), style = VelaText.title, color = colors.textPrimary, modifier = Modifier.weight(1f))
            FormatBadge("FLAC 24/96")
        }
        Text(stringResource(R.string.audio_hint), style = VelaText.caption, color = colors.textTertiary)

        ProgressLine(0.3f)

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoundIconButton(VelaIcons.Shuffle, stringResource(R.string.action_shuffle), {}, size = 44.dp)
            RoundIconButton(VelaIcons.Previous, stringResource(R.string.action_previous), {}, size = 52.dp)
            PlayButton(
                isPlaying = playing,
                contentDescription = stringResource(if (playing) R.string.action_pause else R.string.action_play),
                onClick = { playing = !playing },
            )
            RoundIconButton(VelaIcons.Next, stringResource(R.string.action_next), {}, size = 52.dp)
            RoundIconButton(VelaIcons.Repeat, stringResource(R.string.action_repeat), {}, size = 44.dp)
        }

        VelaCard(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                RoundIconButton(VelaIcons.Speed, null, {}, size = 44.dp)
                RoundIconButton(VelaIcons.Timer, null, {}, size = 44.dp)
                RoundIconButton(VelaIcons.Equalizer, null, {}, size = 44.dp)
                RoundIconButton(VelaIcons.Queue, null, {}, size = 44.dp)
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

/** Декоративные столбики: «дышат», пока включено воспроизведение. Настоящая визуализация — этап 6. */
@Composable
private fun DecorativeBars(playing: Boolean, modifier: Modifier = Modifier) {
    val colors = Vela.colors
    val transition = rememberInfiniteTransition(label = "bars")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Restart),
        label = "phase",
    )
    Canvas(modifier) {
        val count = 24
        val gap = 5.dp.toPx()
        val w = (size.width - gap * (count - 1)) / count
        for (i in 0 until count) {
            val base = 0.25f + 0.2f * sin(i * 0.7f)
            val wave = if (playing) 0.35f * (0.5f + 0.5f * sin(phase * 2 + i * 0.55f)) else 0f
            val h = size.height * (base + wave).coerceIn(0.08f, 1f)
            drawRoundRect(
                color = if (i % 3 == 0) colors.accent else colors.textPrimary.copy(alpha = 0.75f),
                topLeft = Offset(i * (w + gap), size.height - h),
                size = Size(w, h),
                cornerRadius = CornerRadius(w / 2, w / 2),
            )
        }
    }
}
