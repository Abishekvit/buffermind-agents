// File: app/src/main/java/com/samsung/innovatex/buffermind/ai/PredictionInput.kt

package com.samsung.innovatex.buffermind.ai

import com.samsung.innovatex.buffermind.domain.BufferContext

data class PredictionInput(
    val isWalking: Boolean,
    val isMovingGps: Boolean,
    val signalWeak: Boolean,
    val isPlayingRepeated: Boolean,
    val playbackDurationMinutes: Float,
)