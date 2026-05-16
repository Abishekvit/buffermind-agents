package com.samsung.innovatex.buffermind.domain


// File: domain/CacheState.kt

data class CacheState(
    val signalStrength: Float,          // 0–1
    val isWalking: Boolean,             // 0–1
    val isRepeatedPlayback: Boolean,    // 0–1
    val isGpsMoving: Boolean,           // 0–1
    val playbackDurationMinutes: Float,
    val cacheOccupancyPercent: Float,   // 0–1
    val cacheHitRate: Float,            // 0–1
    val avgLatencyMs: Float,            // 0–1000
    val predictedDisconnectProb: Float,  // 0–1
    val trackPopularityScore: Float,    // 0–1
    val timeOfDay: Int,                 // 0–23
    val remainingBufferedSeconds: Float,
)