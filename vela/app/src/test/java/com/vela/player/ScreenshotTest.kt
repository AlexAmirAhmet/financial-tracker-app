package com.vela.player

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.vela.player.audio.AudioPlayerScreen
import com.vela.player.library.LibraryScreen
import com.vela.player.settings.AppSettings
import com.vela.player.settings.SettingsRepository
import com.vela.player.settings.SettingsScreen
import com.vela.player.stream.StreamScreen
import com.vela.player.ui.theme.VelaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Отрисовывает экраны в PNG (app/build/screenshots), чтобы проверить дизайн без телефона.
 * Запуск: ./gradlew :app:testDebugUnitTest --tests '*ScreenshotTest*'
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w393dp-h852dp-xxhdpi")
class ScreenshotTest {

    @get:Rule
    val rule = createComposeRule()

    private fun shot(name: String, content: @Composable () -> Unit) {
        rule.setContent(content)
        rule.waitForIdle()
        val bmp = rule.onRoot().captureToImage().asAndroidBitmap()
        val dir = File("build/screenshots").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }


    @Test fun libraryLight() = shot("library_light") { VelaTheme(false) { LibraryScreen({}, {}, {}, {}) } }
    @Test fun libraryDark() = shot("library_dark") { VelaTheme(true) { LibraryScreen({}, {}, {}, {}) } }
    @Test fun audioLight() = shot("audio_light") { VelaTheme(false) { AudioPlayerScreen {} } }
    @Test fun audioDark() = shot("audio_dark") { VelaTheme(true) { AudioPlayerScreen {} } }
    @Test fun streamLight() = shot("stream_light") { VelaTheme(false) { StreamScreen {} } }
    @Test fun settingsDark() = shot("settings_dark") {
        val repo = SettingsRepository(RuntimeEnvironment.getApplication())
        VelaTheme(true) { SettingsScreen(AppSettings(), repo) {} }
    }
}
