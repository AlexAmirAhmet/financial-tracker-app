package com.vela.player

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.vela.player.library.LibraryScreen
import com.vela.player.library.LibraryViewModel
import com.vela.player.player.PlaybackState
import com.vela.player.player.PlayerError
import com.vela.player.player.PlayerScreen
import com.vela.player.settings.AppSettings
import com.vela.player.settings.SettingsRepository
import com.vela.player.settings.SettingsScreen
import com.vela.player.ui.theme.VelaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp

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

    @Test fun libraryNoAccess() = shot("library_no_access") {
        val vm = LibraryViewModel(RuntimeEnvironment.getApplication())
        VelaTheme(false) { LibraryScreen(vm, {}, {}, {}, {}, {}) }
    }

    @Config(qualifiers = "w852dp-h393dp-xxhdpi")
    @Test fun playerControls() = shot("player_controls") {
        VelaTheme(false) {
            PlayerScreen(
                state = PlaybackState(isPlaying = false, timeMs = 4_400_000, lengthMs = 10_143_000),
                title = "Очень длинное название фильма (2024) 1080p.mkv",
                isVideo = true, error = null, resumeFromMs = 4_360_000,
                onDismissResume = {}, onRestart = {}, onAttach = {}, onPlayPause = {}, onSeek = {}, onBack = {},
            )
        }
    }

    @Config(qualifiers = "w852dp-h393dp-xxhdpi")
    @Test fun playerError() = shot("player_error") {
        VelaTheme(false) {
            PlayerScreen(
                state = PlaybackState(), title = "broken.avi", isVideo = true, error = PlayerError.PLAYBACK, resumeFromMs = null,
                onDismissResume = {}, onRestart = {}, onAttach = {}, onPlayPause = {}, onSeek = {}, onBack = {},
            )
        }
    }

    @Test fun filesAndFolders() = shot("files_folders") {
        fun item(n: String, video: Boolean, dur: Long, size: Long, folder: String) = com.vela.player.library.MediaItem(
            n.hashCode().toLong(), android.net.Uri.parse("content://x/$n"), n, video, dur, size, 0, null, null, folder, folder.trimEnd('/').substringAfterLast('/'),
        )
        val folders = listOf("Movies", "Telegram Video", "Download", "Music", "Сериалы", "Camera").mapIndexed { i, f ->
            com.vela.player.library.MediaFolder("$f/", f, 3 + i * 7, f != "Music")
        }
        VelaTheme(true) {
            com.vela.player.ui.theme.VelaBackground {
                androidx.compose.foundation.layout.Column(androidx.compose.ui.Modifier.padding(24.dp)) {
                    com.vela.player.library.FolderGrid(folders, {})
                    androidx.compose.foundation.layout.Spacer(androidx.compose.ui.Modifier.height(16.dp))
                    com.vela.player.library.MediaRow(item("Дюна. Часть вторая (2024).mkv", true, 9_960_000, 4_400_000_000, "Movies/"), null, {})
                    com.vela.player.library.MediaRow(item("Pink Floyd — Time.flac", false, 413_000, 52_000_000, "Music/"), null, {})
                }
            }
        }
    }

    @Test fun settingsDark() = shot("settings_dark") {
        val repo = SettingsRepository(RuntimeEnvironment.getApplication())
        VelaTheme(true) { SettingsScreen(AppSettings(), repo) {} }
    }
}
