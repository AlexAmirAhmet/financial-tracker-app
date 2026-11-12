package com.vela.player.player

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.vela.player.data.MediaIdentity
import com.vela.player.data.PlaybackPosition
import com.vela.player.library.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Что именно играем: ссылка, название, данные для запоминания позиции. */
data class PlaybackSource(
    val uri: Uri,
    val title: String,
    val isVideo: Boolean,
    val identity: MediaIdentity,
    /** Папка в медиатеке — для «предыдущий/следующий» (этап 4). */
    val folderPath: String?,
)

/** Открытие плеера из приложения и разбор Intent от других приложений. */
object PlayerLauncher {
    private const val EXTRA_TITLE = "vela.title"
    private const val EXTRA_PATH = "vela.path"
    private const val EXTRA_SIZE = "vela.size"
    private const val EXTRA_DURATION = "vela.duration"
    private const val EXTRA_IS_VIDEO = "vela.is_video"
    private const val EXTRA_FOLDER = "vela.folder"

    fun open(context: Context, item: MediaItem) {
        val intent = Intent(context, VideoPlayerActivity::class.java)
            .setDataAndType(item.uri, item.mimeType ?: if (item.isVideo) "video/*" else "audio/*")
            .putExtra(EXTRA_TITLE, item.name)
            .putExtra(EXTRA_PATH, item.path)
            .putExtra(EXTRA_SIZE, item.size)
            .putExtra(EXTRA_DURATION, item.durationMs)
            .putExtra(EXTRA_IS_VIDEO, item.isVideo)
            .putExtra(EXTRA_FOLDER, item.folderPath)
        context.startActivity(intent)
    }

    /** Открыть из блока «Продолжить просмотр». */
    fun open(context: Context, position: PlaybackPosition) {
        val intent = Intent(context, VideoPlayerActivity::class.java)
            .setData(Uri.parse(position.uri))
            .putExtra(EXTRA_TITLE, position.title)
            .putExtra(EXTRA_PATH, position.key.takeIf { it.startsWith("/") })
            .putExtra(EXTRA_IS_VIDEO, position.isVideo)
        position.fingerprint?.split('|')?.let { parts ->
            if (parts.size == 3) {
                intent.putExtra(EXTRA_SIZE, parts[1].toLongOrNull() ?: 0L)
                intent.putExtra(EXTRA_DURATION, (parts[2].toLongOrNull() ?: 0L) * 1000)
            }
        }
        context.startActivity(intent)
    }

    /** Разобрать Intent: из медиатеки всё известно заранее, из других приложений — узнаём сами. */
    suspend fun resolve(context: Context, intent: Intent): PlaybackSource? = withContext(Dispatchers.IO) {
        val uri = intent.data ?: return@withContext null
        val scheme = uri.scheme?.lowercase()
        val isLocal = scheme == "content" || scheme == "file" || scheme == null

        var name = intent.getStringExtra(EXTRA_TITLE)
        var size = intent.getLongExtra(EXTRA_SIZE, 0L)
        var path = intent.getStringExtra(EXTRA_PATH)
        var duration = intent.getLongExtra(EXTRA_DURATION, 0L)

        if (scheme == "content") {
            runCatching {
                context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use { c ->
                    if (c.moveToFirst()) {
                        if (name == null) c.getColumnIndex(OpenableColumns.DISPLAY_NAME).takeIf { it >= 0 && !c.isNull(it) }?.let { name = c.getString(it) }
                        if (size <= 0) c.getColumnIndex(OpenableColumns.SIZE).takeIf { it >= 0 && !c.isNull(it) }?.let { size = c.getLong(it) }
                    }
                }
            }
            if (path == null) path = queryDataPath(context, uri)
        } else if (scheme == "file" || scheme == null) {
            path = path ?: uri.path
            if (name == null) name = uri.lastPathSegment
            if (size <= 0) size = path?.let { java.io.File(it).length() } ?: 0L
        }

        if (isLocal && duration <= 0) duration = readDuration(context, uri)

        val mime = intent.type ?: runCatching { context.contentResolver.getType(uri) }.getOrNull()
        val isVideo = if (intent.hasExtra(EXTRA_IS_VIDEO)) intent.getBooleanExtra(EXTRA_IS_VIDEO, true)
        else !(mime?.startsWith("audio/") ?: false)

        val title = name ?: uri.lastPathSegment?.substringAfterLast('/') ?: uri.toString()
        PlaybackSource(
            uri = uri,
            title = title,
            isVideo = isVideo,
            identity = MediaIdentity(
                uri = uri.toString(),
                title = title,
                path = path?.takeIf { it.startsWith("/") },
                fingerprint = if (isLocal) MediaIdentity.fingerprint(name, size, duration) else null,
                isVideo = isVideo,
            ),
            folderPath = intent.getStringExtra(EXTRA_FOLDER),
        )
    }

    /** Некоторые приложения (файловые менеджеры, «Загрузки») сообщают настоящий путь. */
    @Suppress("DEPRECATION")
    private fun queryDataPath(context: Context, uri: Uri): String? = runCatching {
        context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DATA), null, null, null)?.use { c ->
            if (c.moveToFirst() && !c.isNull(0)) c.getString(0) else null
        }
    }.getOrNull()?.takeIf { it.startsWith("/") }

    private fun readDuration(context: Context, uri: Uri): Long = runCatching {
        MediaMetadataRetriever().run {
            try {
                setDataSource(context, uri)
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            } finally {
                release()
            }
        }
    }.getOrDefault(0L)
}
