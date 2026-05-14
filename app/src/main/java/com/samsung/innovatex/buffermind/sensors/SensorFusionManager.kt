package com.samsung.innovatex.buffermind.sensors

// File: app/src/main/java/com/samsung/innovatex/buffermind/sensors/SensorFusionManager.k

import com.samsung.innovatex.buffermind.domain.PlaybackContext
import com.samsung.innovatex.buffermind.util.Logger

data class FusedContext(
    val isMoving: Boolean,        // GPS + accelerometer
    val signalQuality: SignalQuality,
    val playback: PlaybackContext,
    val riskScore: Float          // 0.0 to 1.0 disconnect risk
)

class SensorFusionManager {

    fun fuseContext(
        gpsMoving: Boolean,
        accelWalking: Boolean,
        signal: SignalQuality,
        playback: PlaybackContext
    ): FusedContext {
        val movementScore = when {
            gpsMoving && accelWalking -> 1.0f
            gpsMoving || accelWalking -> 0.7f
            else -> 0.0f
        }

        val signalScore = when (signal) {
            SignalQuality.Weak -> 0.8f
            SignalQuality.Fair -> 0.4f
            SignalQuality.Strong -> 0.0f
            SignalQuality.Unknown -> 0.6f
        }

        val playbackScore = when (playback) {
            is PlaybackContext.Playing -> 1.0f
            is PlaybackContext.Looping -> 1.2f  // bonus for repeats
            else -> 0.0f
        }

        val riskScore = (movementScore + signalScore + playbackScore) / 3.0f
        Logger.d("Fusion", "Risk: ${String.format("%.2f", riskScore)} (move:$movementScore, sig:$signalScore, play:$playbackScore)")

        return FusedContext(
            isMoving = movementScore > 0.5f,
            signalQuality = signal,
            playback = playback,
            riskScore = riskScore.coerceAtMost(1.0f)
        )
    }
}