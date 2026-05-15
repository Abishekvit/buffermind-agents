// File: app/src/main/java/com/samsung/innovatex/buffermind/ai/FakeLstmPredictor.kt

package com.samsung.innovatex.buffermind.ai

import com.samsung.innovatex.buffermind.domain.BufferContext
import com.samsung.innovatex.buffermind.sensors.SignalLevel
import com.samsung.innovatex.buffermind.feature.player.PlayerState
import com.samsung.innovatex.buffermind.util.Logger

class FakeLstmPredictor {

    private val thresholdBuffer = 0.7

    fun predict(context: BufferContext, isPlayingRepeated: Boolean, playbackDurationMinutes: Float): PredictionResult {
        val isWalking = context.isWalking
        val isPlaying = context.playbackState == PlayerState.Playing
        val signalWeak = context.signalLevel == SignalLevel.Weak
        val isMovingGps = true  // in real version, fused from GPS

        val scores = mutableListOf<Pair<String, Float>>()

        if (isWalking) {
            scores.add("Walking detected" to 0.9f)
        } else {
            scores.add("Stationary" to 0.1f)
        }

        if (signalWeak) {
            scores.add("Weak signal" to 0.9f)
        } else {
            scores.add("Signal acceptable" to 0.2f)
        }

        if (isPlayingRepeated) {
            scores.add("Repeat/looping detected" to 1.0f)
        } else {
            scores.add("Single track" to 0.4f)
        }

        if (isMovingGps) {
            scores.add("GPS movement" to 0.7f)
        }

        if (isPlaying) {
            scores.add("Playback active" to 1.0f)
        } else {
            scores.add("Playback inactive" to 0.1f)
        }

        val totalScore = scores.sumOf { it.second.toDouble() } / scores.size
        val disconnectProb = clamp(totalScore.toFloat(), 0.0f, 1.0f)
        val confidence = 0.9f  // fixed “high confidence” for demo

        val shouldBuffer = disconnectProb >= thresholdBuffer

        val explanation = scores.map { "${it.first} (${String.format("%.2f", it.second)})" }

        Logger.d(
            "FakeLstm",
            "Predicted: ${String.format("%.2f", disconnectProb)} (conf=$confidence), shouldBuffer=$shouldBuffer"
        )

        return PredictionResult(
            disconnectProbability = disconnectProb,
            confidence = confidence,
            shouldBuffer = shouldBuffer,
            explanation = explanation,
        )
    }

    private fun clamp(value: Float, min: Float, max: Float): Float =
        kotlin.math.max(min, kotlin.math.min(value, max))
}