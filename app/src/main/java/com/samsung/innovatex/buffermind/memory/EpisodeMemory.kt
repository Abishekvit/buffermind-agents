package com.samsung.innovatex.buffermind.memory

data class EpisodeMemory(
    val episodeId: String,
    val playbackContext: String,
    val disconnectProbability: Float,
    val signalQuality: String,
    val walkingState: Boolean,
    val gpsMoving: Boolean,
    val playbackDuration: Float,
    val trackTitle: String,
    val cacheHit: Boolean,
    val bufferingSuccess: Boolean,
    val userBehaviorPattern: String,
    val timestamp: String,
)