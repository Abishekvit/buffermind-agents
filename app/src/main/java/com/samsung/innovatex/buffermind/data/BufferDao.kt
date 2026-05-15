// File: app/src/main/java/com/samsung/innovatex/buffermind/data/BufferDao.kt

package com.samsung.innovatex.buffermind.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BufferDao {

    @Query("SELECT * FROM buffered_tracks ORDER BY lastUsedAtMs DESC")
    fun getAll(): Flow<List<BufferedTrackEntity>>

    @Query("SELECT * FROM buffered_tracks WHERE trackId = :trackId LIMIT 1")
    suspend fun getByTrackId(trackId: String): BufferedTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: BufferedTrackEntity)

    @Delete
    suspend fun delete(track: BufferedTrackEntity)

    @Query("DELETE FROM buffered_tracks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM buffered_tracks WHERE id IN (SELECT id FROM buffered_tracks ORDER BY lastUsedAtMs LIMIT :count)")
    suspend fun evictOldest(count: Int)
}