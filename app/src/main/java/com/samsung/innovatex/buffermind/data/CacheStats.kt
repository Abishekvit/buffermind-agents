// File: app/src/main/java/com/samsung/innovatex/buffermind/data/CacheStats.kt

package com.samsung.innovatex.buffermind.data

data class CacheStats(
    val bufferedTrackCount: Int = 0,
    val cacheSizeBytes: Int = 0,
    val cacheHitRate: Float = 0f,
    val evictionCount: Int = 0,
    val totalHits: Int = 0,
    val totalMisses: Int = 0,
)