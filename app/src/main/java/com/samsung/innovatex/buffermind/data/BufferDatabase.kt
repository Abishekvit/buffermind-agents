// File: app/src/main/java/com/samsung/innovatex/buffermind/data/BufferDatabase.kt

package com.samsung.innovatex.buffermind.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(
    entities = [BufferedTrackEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BufferDatabase : RoomDatabase() {

    abstract fun bufferDao(): BufferDao

    companion object {
        @Volatile
        private var INSTANCE: BufferDatabase? = null

        fun getDatabase(context: Context): BufferDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BufferDatabase::class.java,
                    "buffermind_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun migrateTrack(
        trackId: String,
        newLastUsed: Long
    ) = withContext(Dispatchers.IO) {

        val track =
            bufferDao().getByTrackId(trackId)

        if (track != null) {

            val updated =
                track.copy(
                    lastUsedAtMs = newLastUsed
                )

            bufferDao().upsert(updated)
        }
    }
}