package com.vela.player.data

import kotlinx.coroutines.flow.Flow

/** Сведения о файле, по которым ищется и сохраняется позиция. */
data class MediaIdentity(
    val uri: String,
    val title: String,
    /** Полный путь к файлу, если известен. */
    val path: String?,
    val fingerprint: String?,
    val isVideo: Boolean,
) {
    val primaryKey: String get() = path ?: fingerprint ?: uri

    companion object {
        /** «имя|размер|длительность в секундах» — одинаково для медиатеки и других приложений. */
        fun fingerprint(name: String?, size: Long, durationMs: Long): String? =
            if (name.isNullOrBlank() || size <= 0) null else "$name|$size|${durationMs / 1000}"
    }
}

class PositionRepository(private val dao: PositionDao) {

    val all: Flow<List<PlaybackPosition>> = dao.observeAll()
    val continueWatching: Flow<PlaybackPosition?> = dao.observeContinue()

    /** Ищет запись сначала по пути, потом по «отпечатку» (имя + размер + длительность). */
    suspend fun find(id: MediaIdentity): PlaybackPosition? =
        id.path?.let { dao.byKey(it) }
            ?: id.fingerprint?.let { dao.byKey(it) ?: dao.byFingerprint(it) }
            ?: dao.byKey(id.uri)

    suspend fun save(id: MediaIdentity, positionMs: Long, durationMs: Long) {
        val existing = find(id)
        // Если путь стал известен, а раньше запись была по отпечатку — сохраняем под путём.
        val key = id.path ?: existing?.key ?: id.primaryKey
        dao.upsert(
            PlaybackPosition(
                key = key,
                fingerprint = id.fingerprint ?: existing?.fingerprint,
                uri = id.uri,
                title = id.title,
                positionMs = positionMs.coerceAtLeast(0),
                durationMs = durationMs,
                isVideo = id.isVideo,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }
}
