package com.vela.player.library

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Читает видео и аудио из системной медиатеки Android (MediaStore). */
class MediaStoreRepository(private val context: Context) {

    suspend fun loadAll(): List<MediaItem> = withContext(Dispatchers.IO) {
        buildList {
            addAll(query(isVideo = true))
            addAll(query(isVideo = false))
        }
    }

    /** Файлы той же папки — для «предыдущий/следующий» (этап 4). */
    suspend fun loadFolder(folderPath: String): List<MediaItem> =
        loadAll().filter { it.folderPath == folderPath }

    @Suppress("DEPRECATION") // DATA — единственный способ узнать полный путь; читаем, если есть.
    private fun query(isVideo: Boolean): List<MediaItem> {
        val collection: Uri = when {
            isVideo && Build.VERSION.SDK_INT >= 29 -> MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            isVideo -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            Build.VERSION.SDK_INT >= 29 -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val columns = buildList {
            add(MediaStore.MediaColumns._ID)
            add(MediaStore.MediaColumns.DISPLAY_NAME)
            add(MediaStore.MediaColumns.SIZE)
            add(MediaStore.MediaColumns.DATE_MODIFIED)
            add(MediaStore.MediaColumns.MIME_TYPE)
            add(MediaStore.MediaColumns.DATA)
            add(if (isVideo) MediaStore.Video.VideoColumns.DURATION else MediaStore.Audio.AudioColumns.DURATION)
            if (Build.VERSION.SDK_INT >= 29) add(MediaStore.MediaColumns.RELATIVE_PATH)
        }.toTypedArray()

        // Для аудио пропускаем рингтоны, уведомления и будильники.
        val selection = if (isVideo) null else
            "${MediaStore.Audio.AudioColumns.IS_RINGTONE} = 0 AND ${MediaStore.Audio.AudioColumns.IS_NOTIFICATION} = 0 AND ${MediaStore.Audio.AudioColumns.IS_ALARM} = 0"

        val result = mutableListOf<MediaItem>()
        runCatching {
            context.contentResolver.query(collection, columns, selection, null, null)?.use { c ->
                while (c.moveToNext()) {
                    readItem(c, collection, isVideo)?.let(result::add)
                }
            }
        }
        return result
    }

    @Suppress("DEPRECATION")
    private fun readItem(c: Cursor, collection: Uri, isVideo: Boolean): MediaItem? {
        val id = c.getLong(c.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
        val data = c.stringOrNull(MediaStore.MediaColumns.DATA)
        val name = c.stringOrNull(MediaStore.MediaColumns.DISPLAY_NAME)
            ?: data?.substringAfterLast('/') ?: return null
        val durationCol = if (isVideo) MediaStore.Video.VideoColumns.DURATION else MediaStore.Audio.AudioColumns.DURATION
        val relative = if (Build.VERSION.SDK_INT >= 29) c.stringOrNull(MediaStore.MediaColumns.RELATIVE_PATH) else null
        val folderPath = relative ?: data?.let(::relativeFolderFromPath) ?: ""
        val folderName = folderPath.trimEnd('/').substringAfterLast('/').ifEmpty { "/" }
        return MediaItem(
            id = id,
            uri = ContentUris.withAppendedId(collection, id),
            name = name,
            isVideo = isVideo,
            durationMs = c.longOrZero(durationCol),
            size = c.longOrZero(MediaStore.MediaColumns.SIZE),
            dateModified = c.longOrZero(MediaStore.MediaColumns.DATE_MODIFIED),
            mimeType = c.stringOrNull(MediaStore.MediaColumns.MIME_TYPE),
            path = data?.takeIf { it.startsWith("/") },
            folderPath = folderPath,
            folderName = folderName,
        )
    }

    /** «/storage/emulated/0/Movies/Фильмы/a.mkv» → «Movies/Фильмы/». */
    private fun relativeFolderFromPath(path: String): String {
        val dir = path.substringBeforeLast('/', "")
        val rel = Regex("^/storage/(emulated/\\d+|[^/]+)/?").replace(dir, "")
        return if (rel.isEmpty()) "" else "$rel/"
    }

    private fun Cursor.stringOrNull(column: String): String? {
        val i = getColumnIndex(column)
        return if (i >= 0 && !isNull(i)) getString(i) else null
    }

    private fun Cursor.longOrZero(column: String): Long {
        val i = getColumnIndex(column)
        return if (i >= 0 && !isNull(i)) getLong(i) else 0L
    }
}
