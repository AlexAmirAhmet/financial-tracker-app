package com.vela.player.ui

import android.net.Uri
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vela.player.audio.AudioPlayerScreen
import com.vela.player.library.FolderScreen
import com.vela.player.library.LibraryScreen
import com.vela.player.library.LibraryViewModel
import com.vela.player.player.PlayerLauncher
import com.vela.player.settings.AppSettings
import com.vela.player.settings.SettingsRepository
import com.vela.player.settings.SettingsScreen
import com.vela.player.stream.StreamScreen

/** Адреса экранов. */
object Routes {
    const val LIBRARY = "library"
    const val FOLDER = "folder?path={path}"
    const val AUDIO = "audio"
    const val STREAM = "stream"
    const val SETTINGS = "settings"

    fun folder(path: String) = "folder?path=${Uri.encode(path)}"
}

@Composable
fun VelaNavHost(settings: AppSettings, settingsRepository: SettingsRepository) {
    val nav = rememberNavController()
    val context = LocalContext.current
    val back: () -> Unit = { nav.popBackStack() }
    // Общая модель медиатеки для всех экранов (живёт, пока открыто приложение).
    val library: LibraryViewModel = viewModel()

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
                viewModel = library,
                onOpenItem = { PlayerLauncher.open(context, it) },
                onOpenContinue = { PlayerLauncher.open(context, it) },
                onOpenFolder = { nav.navigate(Routes.folder(it.path)) },
                onOpenStream = { nav.navigate(Routes.STREAM) },
                onOpenSettings = { nav.navigate(Routes.SETTINGS) },
            )
        }
        composable(
            Routes.FOLDER,
            arguments = listOf(navArgument("path") { type = NavType.StringType; defaultValue = "" }),
        ) { entry ->
            FolderScreen(
                viewModel = library,
                folderPath = entry.arguments?.getString("path").orEmpty(),
                onBack = back,
                onOpenItem = { PlayerLauncher.open(context, it) },
            )
        }
        composable(Routes.AUDIO) { AudioPlayerScreen(onBack = back) }
        composable(Routes.STREAM) { StreamScreen(onBack = back) }
        composable(Routes.SETTINGS) {
            SettingsScreen(settings = settings, repository = settingsRepository, onBack = back)
        }
    }
}
