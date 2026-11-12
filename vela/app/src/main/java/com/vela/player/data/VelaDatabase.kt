package com.vela.player.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlaybackPosition::class], version = 1, exportSchema = false)
abstract class VelaDatabase : RoomDatabase() {
    abstract fun positionDao(): PositionDao

    companion object {
        fun create(context: Context): VelaDatabase =
            Room.databaseBuilder(context.applicationContext, VelaDatabase::class.java, "vela.db").build()
    }
}
