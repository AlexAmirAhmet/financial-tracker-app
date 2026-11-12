package com.vela.player.player

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vela.player.R
import com.vela.player.ui.theme.PlayButton
import com.vela.player.ui.theme.PlayerColors
import com.vela.player.ui.theme.RoundIconButton
import com.vela.player.ui.theme.VelaIcons
import com.vela.player.ui.theme.VelaText
import com.vela.player.ui.theme.VelaTheme
import com.vela.player.ui.theme.playerPanelBrush

/**
 * Видеоплеер. Этап 1 — пустой макет на тёмном фоне со светлыми «таблетками».
 * libVLC подключается на этапе 3.
 */
class VideoPlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        // По умолчанию — ландшафт (повернуть кнопкой можно будет на этапе 4).
        if (savedInstanceState == null) requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        setContent {
            // Панели плеера всегда светлые, поэтому используем светлую палитру.
            VelaTheme(dark = false) {
                var playing by rememberSaveable { mutableStateOf(false) }
                Box(Modifier.fillMaxSize().background(PlayerColors.frame)) {
                    // Верхняя таблетка.
                    Row(
                        Modifier
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                            .padding(16.dp)
                            .background(PlayerColors.pill, CircleShape)
                            .padding(4.dp)
                            .align(Alignment.TopStart),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RoundIconButton(VelaIcons.Back, stringResource(R.string.action_back), { finish() }, size = 44.dp)
                        Spacer(Modifier.width(10.dp))
                        Text(stringResource(R.string.video_title), style = VelaText.title, color = PlayerColors.pillContent)
                        Spacer(Modifier.width(16.dp))
                    }

                    Text(
                        stringResource(R.string.video_placeholder),
                        style = VelaText.body,
                        color = PlayerColors.pillContent,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(PlayerColors.pill, CircleShape)
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                    )

                    // Нижняя панель с горизонтальным пастельным градиентом.
                    Row(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                            .padding(16.dp)
                            .fillMaxWidth()
                            .background(playerPanelBrush(), CircleShape)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RoundIconButton(VelaIcons.Previous, stringResource(R.string.action_previous), {}, enabled = false)
                        Spacer(Modifier.width(16.dp))
                        PlayButton(
                            isPlaying = playing,
                            contentDescription = stringResource(if (playing) R.string.action_pause else R.string.action_play),
                            onClick = { playing = !playing },
                            size = 60.dp,
                        )
                        Spacer(Modifier.width(16.dp))
                        RoundIconButton(VelaIcons.Next, stringResource(R.string.action_next), {}, enabled = false)
                    }
                }
            }
        }
    }
}
