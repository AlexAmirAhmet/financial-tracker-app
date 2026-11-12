package com.vela.player.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PositionDao {
    @Query("SELECT * FROM positions WHERE `key` = :key LIMIT 1")
    suspend fun byKey(key: String): PlaybackPosition?

    @Query("SELECT * FROM positions WHERE fingerprint = :fingerprint ORDER BY updatedAt DESC LIMIT 1")
    suspend fun byFingerprint(fingerprint: String): PlaybackPosition?

    @Upsert
    suspend fun upsert(position: PlaybackPosition)

    @Query("SELECT * FROM positions")
    fun observeAll(): Flow<List<PlaybackPosition>>

    /** Последнее недосмотренное видео (начато, но не дошло до конца). */
    @Query(
        """SELECT * FROM positions
           WHERE isVideo = 1 AND positionMs > 5000
             AND (durationMs <= 0 OR positionMs < durationMs - MAX(5000, durationMs / 50))
           ORDER BY updatedAt DESC LIMIT 1""",
    )
    fun observeContinue(): Flow<PlaybackPosition?>
}
