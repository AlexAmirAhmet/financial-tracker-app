package com.vela.player.ui

import android.content.Intent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vela.player.audio.AudioPlayerScreen
import com.vela.player.library.LibraryScreen
import com.vela.player.player.VideoPlayerActivity
import com.vela.player.settings.AppSettings
import com.vela.player.settings.SettingsRepository
import com.vela.player.settings.SettingsScreen
import com.vela.player.stream.StreamScreen

/** Адреса экранов. */
object Routes {
    const val LIBRARY = "library"
    const val AUDIO = "audio"
    const val STREAM = "stream"
    const val SETTINGS = "settings"
}

@Composable
fun VelaNavHost(settings: AppSettings, settingsRepository: SettingsRepository) {
    val nav = rememberNavController()
    val context = LocalContext.current
    val back: () -> Unit = { nav.popBackStack() }

    NavHost(
        navController = nav,
        startDestination = Routes.LIBRARY,
        enterTransition = { fadeIn() + slideInHorizontally { it / 8 } },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() + slideOutHorizontally { it / 8 } },
    ) {
        composable(Routes.LIBRARY) {
            LibraryScreen(
                onOpenVideo = { context.startActivity(Intent(context, VideoPlayerActivity::class.java)) },
                onOpenAudio = { nav.navigate(Routes.AUDIO) },
                onOpenStream = { nav.navigate(Routes.STREAM) },
                onOpenSettings = { nav.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.AUDIO) { AudioPlayerScreen(onBack = back) }
        composable(Routes.STREAM) { StreamScreen(onBack = back) }
        composable(Routes.SETTINGS) {
            SettingsScreen(settings = settings, repository = settingsRepository, onBack = back)
        }
    }
}
