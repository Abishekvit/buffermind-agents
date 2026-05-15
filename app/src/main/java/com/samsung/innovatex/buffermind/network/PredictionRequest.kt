package com.samsung.innovatex.buffermind.network

data class PredictionRequest(
    val walking: Boolean,
    val weak_signal: Boolean,
    val repeated_playback: Boolean,
    val gps_moving: Boolean,
    val playback_duration_minutes: Float,
)