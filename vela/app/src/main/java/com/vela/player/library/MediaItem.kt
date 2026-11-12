package com.vela.player.library

import android.net.Uri
import com.vela.player.data.MediaIdentity

/** Медиафайл из MediaStore. */
data class MediaItem(
    val id: Long,
    val uri: Uri,
    val name: String,
    val isVideo: Boolean,
    val durationMs: Long,
    val size: Long,
    /** Дата изменения, секунды. */
    val dateModified: Long,
    val mimeType: String?,
    /** Полный путь, если система его сообщает. */
    val path: String?,
    /** Папка: относительный путь вида «Movies/Фильмы/». */
    val folderPath: String,
    val folderName: String,
) {
    /** Расширение для плашки формата: MP4, MKV, FLAC… */
    val extension: String
        get() = name.substringAfterLast('.', "").uppercase().take(5).ifEmpty { mimeType?.substringAfter('/')?.uppercase()?.take(5) ?: "?" }

    val identity: MediaIdentity
        get() = MediaIdentity(
            uri = uri.toString(),
            title = name,
            path = path,
            fingerprint = MediaIdentity.fingerprint(name, size, durationMs),
            isVideo = isVideo,
        )
}

/** Папка медиатеки. */
data class MediaFolder(
    val path: String,
    val name: String,
    val count: Int,
    val hasVideo: Boolean,
)
