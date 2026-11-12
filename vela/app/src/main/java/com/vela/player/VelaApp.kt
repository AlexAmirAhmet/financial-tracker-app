package com.vela.player

import android.app.Application
import com.vela.player.settings.SettingsRepository

/**
 * Класс приложения. Здесь живут общие на всё приложение объекты.
 * Единственный экземпляр LibVLC (ленивый) добавится сюда на этапе 3.
 */
class VelaApp : Application() {
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }
}
