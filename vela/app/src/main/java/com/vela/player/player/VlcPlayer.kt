package com.vela.player.player

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.vela.player.settings.DecoderMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

/** Состояние воспроизведения для интерфейса. */
data class PlaybackState(
    val isPlaying: Boolean = false,
    val timeMs: Long = 0,
    val lengthMs: Long = 0,
    /** 0–100; меньше 100 — идёт буферизация. */
    val buffering: Float = 100f,
    val ended: Boolean = false,
    val error: Boolean = false,
    val seekable: Boolean = true,
)

/**
 * Обёртка над MediaPlayer из libVLC. Использует общий экземпляр [LibVLC] приложения.
 * После [release] объект использовать нельзя.
 */
class VlcPlayer(libVLC: LibVLC) {

    private val libVLC = libVLC
    private val player = MediaPlayer(libVLC)
    private var fileDescriptor: ParcelFileDescriptor? = null
    private var layout: VLCVideoLayout? = null
    private var attached = false
    private var playWhenAttached = false
    private var released = false
    /** Куда перемотать, как только воспроизведение начнётся (возобновление просмотра). */
    private var pendingSeekMs = 0L

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    init {
        player.setEventListener { e ->
            when (e.type) {
                MediaPlayer.Event.Playing -> {
                    if (pendingSeekMs > 0) {
                        player.time = pendingSeekMs
                        pendingSeekMs = 0
                    }
                    _state.update { it.copy(isPlaying = true, ended = false, error = false) }
                }
                MediaPlayer.Event.Paused, MediaPlayer.Event.Stopped -> _state.update { it.copy(isPlaying = false) }
                MediaPlayer.Event.EndReached -> _state.update { it.copy(isPlaying = false, ended = true, timeMs = it.lengthMs) }
                MediaPlayer.Event.EncounteredError -> _state.update { it.copy(isPlaying = false, error = true) }
                MediaPlayer.Event.TimeChanged -> _state.update { it.copy(timeMs = e.timeChanged) }
                MediaPlayer.Event.LengthChanged -> _state.update { it.copy(lengthMs = e.lengthChanged) }
                MediaPlayer.Event.Buffering -> _state.update { it.copy(buffering = e.buffering) }
                MediaPlayer.Event.SeekableChanged -> _state.update { it.copy(seekable = e.seekable) }
            }
        }
    }

    /** Подключить поверхность для видео (вызывается, когда экран готов). */
    fun attach(view: VLCVideoLayout) {
        if (released) return
        if (layout !== view && attached) {
            player.detachViews()
            attached = false
        }
        layout = view
        reattach()
    }

    /** Снова подключить видео после возврата в приложение. */
    fun reattach() {
        val view = layout ?: return
        if (released || attached) return
        player.attachViews(view, null, true, false)
        attached = true
        if (playWhenAttached) {
            playWhenAttached = false
            player.play()
        }
    }

    /** Отключить видео (когда экран не виден — поверхность уничтожается системой). */
    fun detach() {
        if (released || !attached) return
        player.detachViews()
        attached = false
    }

    /**
     * Открыть файл или ссылку. Бросает исключение, если файл нельзя открыть
     * (нет доступа, файл удалён) — вызывающий код показывает понятную ошибку.
     */
    fun open(context: Context, uri: Uri, decoder: DecoderMode, startMs: Long) {
        check(!released)
        closeDescriptor()
        val media = when (uri.scheme) {
            "content" -> {
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                    ?: error("Не удалось открыть $uri")
                fileDescriptor = pfd
                Media(libVLC, pfd.fileDescriptor)
            }
            "file", null -> Media(libVLC, uri.path ?: uri.toString())
            else -> Media(libVLC, uri)
        }
        when (decoder) {
            // Аппаратное декодирование, вывод через libVLC.
            DecoderMode.HW -> {
                media.setHWDecoderEnabled(true, false)
                media.addOption(":no-mediacodec-dr")
                media.addOption(":no-omxil-dr")
            }
            // Полностью аппаратно: декодирование и вывод напрямую.
            DecoderMode.HW_PLUS -> media.setHWDecoderEnabled(true, false)
            // Программное декодирование — медленнее, но читает «капризные» файлы.
            DecoderMode.SW -> media.setHWDecoderEnabled(false, false)
        }
        pendingSeekMs = startMs
        player.media = media
        media.release() // плеер держит свою ссылку на Media
        _state.value = PlaybackState()
        if (attached) player.play() else playWhenAttached = true
    }

    fun play() {
        if (released) return
        if (_state.value.ended) {
            // После конца файла libVLC останавливается — запускаем заново с начала.
            _state.update { it.copy(ended = false, timeMs = 0) }
        }
        if (attached) player.play() else playWhenAttached = true
    }

    fun pause() {
        if (released) return
        playWhenAttached = false
        if (player.isPlaying) player.pause()
    }

    fun togglePlayPause() = if (_state.value.isPlaying) pause() else play()

    fun seekTo(ms: Long) {
        if (released) return
        val len = _state.value.lengthMs
        val target = if (len > 0) ms.coerceIn(0, len) else ms.coerceAtLeast(0)
        if (_state.value.ended) {
            pendingSeekMs = target
            play()
        } else {
            pendingSeekMs = 0
            player.time = target
        }
        _state.update { it.copy(timeMs = target) }
    }

    /** Освободить ресурсы libVLC. Сам экземпляр LibVLC общий и не освобождается. */
    fun release() {
        if (released) return
        released = true
        player.setEventListener(null)
        player.stop()
        if (attached) player.detachViews()
        attached = false
        player.media?.release()
        player.release()
        closeDescriptor()
        layout = null
    }

    private fun closeDescriptor() {
        runCatching { fileDescriptor?.close() }
        fileDescriptor = null
    }
}
