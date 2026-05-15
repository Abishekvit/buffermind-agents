// File: app/src/main/java/com/samsung/innovatex/buffermind/data/AdaptiveMemoryManager.kt

package com.samsung.innovatex.buffermind.data

import com.samsung.innovatex.buffermind.domain.BufferContext
import com.samsung.innovatex.buffermind.sensors.SignalQuality
import com.samsung.innovatex.buffermind.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.Instant

data class CacheStats(
    val bufferedTrackCount: Int,
    val cacheSizeBytes: Int,         // mock: each 30‑min ≈ 50 MB
    val cacheHitRate: Float,        // 0–1
    val evictionCount: Int,
    val totalHits: Int,
    val totalMisses: Int,
)

class AdaptiveMemoryManager(
    private val dao: BufferDao,
    private val context: android.content.Context,
    private val maxBufferedSeconds: Int = 30 * 60, // 30 min = 1800 s
    private val maxTrackCount: Int = 10,
    private val onCacheStatsChanged: (CacheStats) -> Unit = {},
) {

    private var totalHits = 0
    private var totalMisses = 0

    suspend fun bufferTrackIfNotPresent(
        context: BufferContext,
        trackId: String,
        title: String,
        signal: SignalQuality,
        isPlayingRepeated: Boolean,
        durationSec: Int = 1800,  // 30 minutes
    ) = withContext(Dispatchers.IO) {
        val existing = dao.getByTrackId(trackId)
        if (existing != null) {
            totalHits++
            Logger.d("Cache", "Cache HIT: track='$title' (already buffered)")
            updateLastUsed(trackId, System.currentTimeMillis())
        } else {
            totalMisses++
            val now = System.currentTimeMillis()
            val entity = BufferedTrackEntity(
                trackId = trackId,
                title = title,
                bufferedAtMs = now,
                lastUsedAtMs = now,
                durationSec = durationSec,
                isPlayingRepeated = isPlayingRepeated,
                signalWhenBuffered = signal.toString(),
            )
            dao.upsert(entity)
            Logger.d("Cache", "Cache MISS: buffered track='$title'")

            // After insert, check if we exceeded limits and evict
            evictIfNeeded()
        }
        onCacheStatsChanged(computeStats())
    }

    private suspend fun updateLastUsed(trackId: String, timestampMs: Long) {
        val existing = dao.getByTrackId(trackId)

        if (existing != null) {

            val updated =
                existing.copy(lastUsedAtMs = timestampMs)

            dao.upsert(updated)
        }
    }

    suspend fun markTrackUsed(trackId: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        totalHits++
        updateLastUsed(trackId, now)
        Logger.d("Cache", "Used cached track='$trackId' at $now")
        onCacheStatsChanged(computeStats())
    }

    suspend fun evictIfNeeded() = withContext(Dispatchers.IO) {
        val tracks = dao.getAll().first()
        if (tracks.size > maxTrackCount) {
            val countToEvict = tracks.size - maxTrackCount
            dao.evictOldest(countToEvict)
            Logger.d("Cache", "LRU evicted $countToEvict oldest tracks")
        }
    }

    suspend fun computeStats(): CacheStats = withContext(Dispatchers.IO) {
        val tracks = dao.getAll().first()
        val count = tracks.size
        val sizeBytes = count * 50 * 1024 * 1024  // 50 MB per 30‑min track (demo only)

        val cacheHits = totalHits
        val totalAccess = totalHits + totalMisses
        val hitRate = if (totalAccess > 0) cacheHits / totalAccess.toFloat() else 0f

        CacheStats(
            bufferedTrackCount = count,
            cacheSizeBytes = sizeBytes,
            cacheHitRate = hitRate,
            evictionCount = 0,  // extend later
            totalHits = cacheHits,
            totalMisses = totalMisses,
        )
    }

    suspend fun printStats() {
        val stats = computeStats()
        Logger.d(
            "Cache",
            "Buffer count: ${stats.bufferedTrackCount}, " +
                    "Size: ${stats.cacheSizeBytes / 1024 / 1024} MB, " +
                    "Hit rate: ${String.format("%.2f", stats.cacheHitRate)}"
        )
    }
}