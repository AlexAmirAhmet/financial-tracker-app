package com.vela.player.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Запомненная позиция воспроизведения файла.
 *
 * [key] — путь к файлу, если он известен, иначе [fingerprint].
 * [fingerprint] — «имя|размер|длительность в секундах»: по нему находится тот же файл,
 * открытый из другого приложения (например, из Telegram), где путь неизвестен.
 */
@Entity(tableName = "positions", indices = [Index("fingerprint"), Index("updatedAt")])
data class PlaybackPosition(
    @PrimaryKey val key: String,
    val fingerprint: String?,
    val uri: String,
    val title: String,
    val positionMs: Long,
    val durationMs: Long,
    val isVideo: Boolean,
    val updatedAt: Long,
) {
    /** Досмотрен ли файл (до конца осталось меньше 2% или 5 секунд). */
    val isFinished: Boolean
        get() = durationMs > 0 && positionMs >= durationMs - maxOf(5_000L, durationMs / 50)

    val progress: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
}
