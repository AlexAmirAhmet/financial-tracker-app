package com.vela.player

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vela.player.ui.VelaNavHost
import com.vela.player.ui.theme.VelaTheme
import com.vela.player.ui.theme.isDarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repo = (application as VelaApp).settingsRepository

        setContent {
            // Пока настройки не прочитаны (доли секунды) — ничего не рисуем, чтобы тема не «мигала».
            val settings by repo.settings.collectAsStateWithLifecycle(initialValue = null)
            val current = settings ?: return@setContent
            val dark = isDarkTheme(current.themeMode)

            // Цвет значков статус-бара и навигации под выбранную тему.
            LaunchedEffect(dark) {
                val style = if (dark) SystemBarStyle.dark(Color.TRANSPARENT)
                else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            }

            VelaTheme(dark = dark) {
                VelaNavHost(settings = current, settingsRepository = repo)
            }
        }
    }
}
