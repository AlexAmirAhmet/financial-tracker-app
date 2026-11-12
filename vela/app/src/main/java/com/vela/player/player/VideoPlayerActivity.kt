package com.vela.player.player

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.vela.player.VelaApp
import com.vela.player.data.MediaIdentity
import com.vela.player.ui.theme.VelaTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Видеоплеер на libVLC. Открывает файлы из медиатеки и из других приложений
 * (файловые менеджеры, Telegram), запоминает позицию каждого файла.
 */
class VideoPlayerActivity : ComponentActivity() {

    private val app get() = application as VelaApp
    private lateinit var player: VlcPlayer

    private var source by mutableStateOf<PlaybackSource?>(null)
    private var error by mutableStateOf<PlayerError?>(null)
    private var resumeFromMs by mutableStateOf<Long?>(null)

    private var openJob: Job? = null
    private var saveJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        hideSystemBars()

        player = VlcPlayer(app.libVLC)

        setContent {
            val state by player.state.collectAsStateWithLifecycle()
            val src = source
            // Ошибка из libVLC (повреждённый файл, неизвестный формат).
            val shownError = error ?: if (state.error) PlayerError.PLAYBACK else null
            // Экран не гаснет, пока идёт воспроизведение.
            if (state.isPlaying) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            VelaTheme(dark = false) {
                PlayerScreen(
                    state = state,
                    title = src?.title ?: "",
                    isVideo = src?.isVideo ?: true,
                    error = shownError,
                    resumeFromMs = resumeFromMs,
                    onDismissResume = { resumeFromMs = null },
                    onRestart = {
                        resumeFromMs = null
                        player.seekTo(0)
                        player.play()
                    },
                    onAttach = player::attach,
                    onPlayPause = player::togglePlayPause,
                    onSeek = player::seekTo,
                    onBack = { finish() },
                )
            }
        }

        handleIntent(intent)
        watchPlayback()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        savePosition()
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        openJob?.cancel()
        error = null
        resumeFromMs = null
        openJob = lifecycleScope.launch {
            val src = PlayerLauncher.resolve(this@VideoPlayerActivity, intent)
            if (src == null) {
                error = PlayerError.CANNOT_OPEN
                return@launch
            }
            source = src
            // Музыку не поворачиваем принудительно в ландшафт.
            requestedOrientation = if (src.isVideo) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            val saved = app.positionRepository.find(src.identity)
            val start = saved?.takeIf { !it.isFinished && it.positionMs > 5_000 }?.positionMs ?: 0L
            val settings = app.settingsRepository.settings.first()
            try {
                player.open(this@VideoPlayerActivity, src.uri, settings.decoder, start)
                if (start > 0) resumeFromMs = start
            } catch (e: Exception) {
                // Файл удалён, нет доступа, приложение-источник отозвало ссылку.
                error = PlayerError.CANNOT_OPEN
            }
        }
    }

    /** Периодически сохраняем позицию и отмечаем досмотренные файлы. */
    private fun watchPlayback() {
        saveJob = lifecycleScope.launch {
            while (isActive) {
                delay(5_000)
                if (player.state.value.isPlaying) savePosition()
            }
        }
        lifecycleScope.launch {
            // Файл доигран до конца — отмечаем как досмотренный.
            player.state.collect { s -> if (s.ended) savePosition() }
        }
    }

    private fun savePosition() {
        val src = source ?: return
        val s = player.state.value
        if (error != null || s.error) return
        val length = s.lengthMs
        if (length <= 0 && s.timeMs <= 0) return
        // Сетевые потоки без длительности не запоминаем.
        if (length <= 0 && src.identity.fingerprint == null && src.identity.path == null) return
        val position = if (s.ended) length else s.timeMs
        val identity: MediaIdentity = src.identity
        app.appScope.launch { app.positionRepository.save(identity, position, length) }
    }

    override fun onStart() {
        super.onStart()
        player.reattach()
    }

    override fun onStop() {
        // Фоновое воспроизведение и «картинка в картинке» — на этапе 4.
        player.pause()
        savePosition()
        player.detach()
        super.onStop()
    }

    override fun onDestroy() {
        saveJob?.cancel()
        player.release()
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    /** Полноэкранный режим: панели системы появляются свайпом от края. */
    private fun hideSystemBars() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}
