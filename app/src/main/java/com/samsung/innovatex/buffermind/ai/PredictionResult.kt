// File: app/src/main/java/com/samsung/innovatex/buffermind/ai/PredictionResult.kt

package com.samsung.innovatex.buffermind.ai

data class PredictionResult(
    val disconnectProbability: Float,
    val confidence: Float,
    val shouldBuffer: Boolean,
    val explanation: List<String>,
)