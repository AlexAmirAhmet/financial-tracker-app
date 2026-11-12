package com.vela.player.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.vela.player.R
import com.vela.player.library.formatTime
import com.vela.player.ui.theme.LightVelaColors
import com.vela.player.ui.theme.PlayButton
import com.vela.player.ui.theme.PlayerColors
import com.vela.player.ui.theme.RoundIconButton
import com.vela.player.ui.theme.VelaIcons
import com.vela.player.ui.theme.VelaText
import com.vela.player.ui.theme.playerPanelBrush
import kotlinx.coroutines.delay
import org.videolan.libvlc.util.VLCVideoLayout

/** Ошибка открытия: файл не найден / нет доступа / повреждён. */
enum class PlayerError { CANNOT_OPEN, PLAYBACK }

@Composable
fun PlayerScreen(
    state: PlaybackState,
    title: String,
    isVideo: Boolean,
    error: PlayerError?,
    resumeFromMs: Long?,
    onDismissResume: () -> Unit,
    onRestart: () -> Unit,
    onAttach: (VLCVideoLayout) -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onBack: () -> Unit,
) {
    var controlsVisible by remember { mutableStateOf(true) }
    var interaction by remember { mutableIntStateOf(0) }
    var dragging by remember { mutableStateOf(false) }
    val touched = { interaction++ }

    // Автоскрытие управления через 3 секунды, пока идёт воспроизведение.
    LaunchedEffect(controlsVisible, interaction, state.isPlaying, dragging) {
        if (controlsVisible && state.isPlaying && !dragging) {
            delay(3_000)
            controlsVisible = false
        }
    }
    // Плашка «Продолжаем с …» исчезает сама.
    LaunchedEffect(resumeFromMs) {
        if (resumeFromMs != null) {
            delay(7_000)
            onDismissResume()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(PlayerColors.frame)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { controlsVisible = !controlsVisible; interaction++ })
            },
    ) {
        AndroidView(
            factory = { ctx -> VLCVideoLayout(ctx).also(onAttach) },
            modifier = Modifier.fillMaxSize(),
        )

        if (!isVideo && error == null) {
            AudioOnlyArt(title, Modifier.align(Alignment.Center))
        }

        val waiting = error == null && !state.ended && state.buffering < 100f && !state.isPlaying
        if (waiting) {
            CircularProgressIndicator(
                color = PlayerColors.pill,
                strokeWidth = 2.dp,
                modifier = Modifier.align(Alignment.Center).size(44.dp),
            )
        }

        // Верхняя «таблетка»: назад и название.
        AnimatedVisibility(
            visible = controlsVisible || error != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopStart),
        ) {
            Row(
                Modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(16.dp)
                    .widthIn(max = 560.dp)
                    .background(PlayerColors.pill, CircleShape)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoundIconButton(VelaIcons.Back, stringResource(R.string.action_back), onBack, size = 44.dp)
                Spacer(Modifier.width(10.dp))
                Text(
                    title,
                    style = VelaText.title,
                    color = PlayerColors.pillContent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(16.dp))
            }
        }

        // Нижняя панель с пастельным градиентом.
        AnimatedVisibility(
            visible = (controlsVisible || state.ended) && error == null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            ControlPanel(
                state = state,
                onPlayPause = { onPlayPause(); touched() },
                onSeek = { onSeek(it); touched() },
                onDragging = { dragging = it; touched() },
            )
        }

        // «Продолжаем с 1:12:40» + «С начала».
        AnimatedVisibility(
            visible = resumeFromMs != null && error == null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Row(
                Modifier
                    .background(PlayerColors.pill, CircleShape)
                    .padding(start = 20.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.player_resume_from, formatTime(resumeFromMs ?: 0)),
                    style = VelaText.body,
                    color = PlayerColors.pillContent,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    stringResource(R.string.player_from_start),
                    style = VelaText.chip,
                    color = Color.White,
                    modifier = Modifier
                        .background(PlayerColors.pillContent, CircleShape)
                        .clickable(onClick = onRestart)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
        }

        if (error != null) {
            ErrorCard(error, onBack, Modifier.align(Alignment.Center))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ControlPanel(
    state: PlaybackState,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onDragging: (Boolean) -> Unit,
) {
    var dragValue by remember { mutableFloatStateOf(-1f) }
    val length = state.lengthMs
    val shownTime = if (dragValue >= 0f) (dragValue * length).toLong() else state.timeMs
    val dark = LightVelaColors.textPrimary

    Column(
        Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .widthIn(max = 760.dp)
            .fillMaxWidth()
            .background(playerPanelBrush(), RoundedCornerShape(32.dp))
            // Касания по панели не должны скрывать управление.
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(formatTime(shownTime), style = VelaText.caption, color = dark, modifier = Modifier.width(56.dp), textAlign = TextAlign.Start)
            Slider(
                value = if (length > 0) (if (dragValue >= 0f) dragValue else state.timeMs.toFloat() / length).coerceIn(0f, 1f) else 0f,
                onValueChange = { dragValue = it; onDragging(true) },
                onValueChangeFinished = {
                    if (dragValue >= 0f && length > 0) onSeek((dragValue * length).toLong())
                    dragValue = -1f
                    onDragging(false)
                },
                enabled = length > 0 && state.seekable,
                colors = SliderDefaults.colors(
                    thumbColor = dark,
                    activeTrackColor = dark,
                    inactiveTrackColor = dark.copy(alpha = 0.18f),
                    disabledThumbColor = dark.copy(alpha = 0.3f),
                    disabledActiveTrackColor = dark.copy(alpha = 0.3f),
                    disabledInactiveTrackColor = dark.copy(alpha = 0.12f),
                ),
                thumb = {
                    Box(Modifier.size(16.dp).background(dark, CircleShape))
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.height(4.dp),
                        colors = SliderDefaults.colors(activeTrackColor = dark, inactiveTrackColor = dark.copy(alpha = 0.18f)),
                        thumbTrackGapSize = 0.dp,
                        drawStopIndicator = null,
                    )
                },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            )
            Text(
                if (length > 0) formatTime(length) else "--:--",
                style = VelaText.caption,
                color = dark,
                modifier = Modifier.width(56.dp),
                textAlign = TextAlign.End,
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // «Предыдущий/следующий» включатся на этапе 4.
            RoundIconButton(VelaIcons.Previous, stringResource(R.string.action_previous), {}, enabled = false)
            Spacer(Modifier.width(20.dp))
            PlayButton(
                isPlaying = state.isPlaying,
                contentDescription = stringResource(if (state.isPlaying) R.string.action_pause else R.string.action_play),
                onClick = onPlayPause,
                size = 60.dp,
            )
            Spacer(Modifier.width(20.dp))
            RoundIconButton(VelaIcons.Next, stringResource(R.string.action_next), {}, enabled = false)
        }
    }
}

@Composable
private fun AudioOnlyArt(title: String, modifier: Modifier = Modifier) {
    Column(modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(96.dp).background(PlayerColors.pill.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(VelaIcons.Music, null, Modifier.size(44.dp), tint = PlayerColors.pill)
        }
        Spacer(Modifier.height(16.dp))
        Text(title, style = VelaText.title, color = PlayerColors.pill, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun ErrorCard(error: PlayerError, onClose: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp)
            .widthIn(max = 420.dp)
            .background(PlayerColors.pill, RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.player_error_title), style = VelaText.handwrittenMedium, color = PlayerColors.pillContent)
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(if (error == PlayerError.CANNOT_OPEN) R.string.player_error_open else R.string.player_error_playback),
            style = VelaText.body,
            color = LightVelaColors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.action_close),
            style = VelaText.chip,
            color = Color.White,
            modifier = Modifier
                .background(PlayerColors.pillContent, CircleShape)
                .clickable(onClick = onClose)
                .padding(horizontal = 24.dp, vertical = 14.dp),
        )
    }
}
