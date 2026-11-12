package com.vela.player.library

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/** Насколько приложению видны медиафайлы. */
enum class MediaAccess {
    /** Доступа нет. */
    NONE,
    /** Android 14+: пользователь разрешил только часть файлов. */
    PARTIAL,
    FULL,
}

object MediaPermissions {

    /** Какие разрешения запрашивать на этой версии Android. */
    val toRequest: Array<String>
        get() = when {
            Build.VERSION.SDK_INT >= 34 -> arrayOf(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            )
            Build.VERSION.SDK_INT >= 33 -> arrayOf(Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO)
            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    fun access(context: Context): MediaAccess {
        fun granted(p: String) = ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED
        if (Build.VERSION.SDK_INT < 33) {
            return if (granted(Manifest.permission.READ_EXTERNAL_STORAGE)) MediaAccess.FULL else MediaAccess.NONE
        }
        val video = granted(Manifest.permission.READ_MEDIA_VIDEO)
        val audio = granted(Manifest.permission.READ_MEDIA_AUDIO)
        val selected = Build.VERSION.SDK_INT >= 34 && granted(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        return when {
            video && audio -> MediaAccess.FULL
            video || audio || selected -> MediaAccess.PARTIAL
            else -> MediaAccess.NONE
        }
    }
}
