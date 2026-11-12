package com.vela.player

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.video.VideoFrameDecoder
import com.vela.player.data.PositionRepository
import com.vela.player.data.VelaDatabase
import com.vela.player.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.videolan.libvlc.LibVLC

/** Класс приложения: общие на всё приложение объекты. */
class VelaApp : Application(), SingletonImageLoader.Factory {

    /** Фоновые задачи, которые должны доработать даже после закрытия экрана (сохранение позиции). */
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }

    val database: VelaDatabase by lazy { VelaDatabase.create(this) }
    val positionRepository: PositionRepository by lazy { PositionRepository(database.positionDao()) }

    /**
     * Единственный экземпляр LibVLC на всё приложение (видео и аудио).
     * Создаётся лениво — при первом открытии плеера.
     */
    val libVLC: LibVLC by lazy {
        LibVLC(
            this,
            arrayListOf(
                "--audio-time-stretch", // скорость без «мультяшного» голоса
                "--http-reconnect",
            ),
        )
    }

    /** Загрузчик превью: умеет брать кадр из видео. */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(VideoFrameDecoder.Factory()) }
            .build()
}
