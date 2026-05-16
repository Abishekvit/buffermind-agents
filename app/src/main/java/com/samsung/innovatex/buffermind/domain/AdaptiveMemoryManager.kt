package com.samsung.innovatex.buffermind.domain
import com.samsung.innovatex.buffermind.ai.CachePolicyAgent
// domain/AdaptiveMemoryManager.kt → extend with RL
import com.samsung.innovatex.buffermind.data.BufferDao
import com.samsung.innovatex.buffermind.data.CacheStats
import com.samsung.innovatex.buffermind.domain.BufferContext
import com.samsung.innovatex.buffermind.data.BufferedTrackEntity
import com.samsung.innovatex.buffermind.sensors.SignalQuality
import com.samsung.innovatex.buffermind.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*

class AdaptiveMemoryManager(
    private val dao: BufferDao,
    private val context: android.content.Context,
    private val maxTrackCount: Int = 10,
    private val maxBufferedSeconds: Int = 30 * 60, // 30 min
    private val onCacheStatsChanged: (CacheStats) -> Unit = {},
) {

    private var totalHits = 0
    private var totalMisses = 0
    private var evictionCount = 0

    suspend fun bufferTrackIfNotPresent(
        context: BufferContext,
        trackId: String,
        title: String,
        signal: SignalQuality,
        isPlayingRepeated: Boolean,
        durationSec: Int = 1800,  // 30 minutes
    ) = withContext(Dispatchers.IO) {
        val existing = dao.getByTrackId(trackId).first()
        if (existing != null) {
            totalHits++
            Logger.d("AdaptiveMemory", "CACHE HIT for track='$title'")
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
            Logger.d("AdaptiveMemory", "CACHE MISS for track='$title' – buffered")
            evictIfNeeded()
        }
        onCacheStatsChanged(computeStats())
    }

    suspend fun markTrackUsed(trackId: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        totalHits++
        updateLastUsed(trackId, now)
        Logger.d("AdaptiveMemory", "Used cached track='$trackId' at $now")
        onCacheStatsChanged(computeStats())
    }

    private suspend fun updateLastUsed(
        trackId: String,
        timestampMs: Long
    ) {

        val existing =
            dao.getByTrackId(trackId).first()

        if (existing != null) {

            val updated =
                existing.copy(
                    lastUsedAtMs = timestampMs
                )

            dao.upsert(updated)
        }
    }

    suspend fun evictIfNeeded() = withContext(Dispatchers.IO) {
        val tracks = dao.getAll().first()
        if (tracks.size > maxTrackCount) {
            val countToEvict = tracks.size - maxTrackCount
            dao.evictOldest(countToEvict)
            evictionCount += countToEvict
            Logger.d("AdaptiveMemory", "LRU evicted $countToEvict oldest tracks")
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
            evictionCount = evictionCount,
            totalHits = cacheHits,
            totalMisses = totalMisses,
        )
    }

    suspend fun printStats() {
        val stats = computeStats()
        Logger.d(
            "AdaptiveMemory",
            "Count: ${stats.bufferedTrackCount}, " +
                    "Size: ${stats.cacheSizeBytes / 1024 / 1024} MB, " +
                    "Hit rate: ${String.format("%.2f", stats.cacheHitRate)}"
        )
    }
}
