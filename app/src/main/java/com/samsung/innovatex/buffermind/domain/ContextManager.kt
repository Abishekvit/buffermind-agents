// File: app/src/main/java/com/samsung/innovatex/buffermind/domain/ContextManager.kt

package com.samsung.innovatex.buffermind.domain

import com.samsung.innovatex.buffermind.sensors.SignalLevel
import com.samsung.innovatex.buffermind.feature.player.PlayerState
import com.samsung.innovatex.buffermind.sensors.WalkingDetector
import com.samsung.innovatex.buffermind.sensors.SignalStrengthDetector
import com.samsung.innovatex.buffermind.util.Logger

class ContextManager(
    private val walkingDetector: WalkingDetector,
    private val signalStrengthDetector: SignalStrengthDetector
) {

    private var currentPlaybackState = PlayerState.Idle
    private var currentSignalLevel = SignalLevel.Unknown

    init {
        signalStrengthDetector.onChange = { level ->
            currentSignalLevel = level
            Logger.d("Context", "Signal level: $level")
        }
    }

    fun updatePlaybackState(state: PlayerState) {
        currentPlaybackState = state
    }

    fun updateWalking(accelEvent: android.hardware.SensorEvent): BufferContext {
        val isWalking = walkingDetector.onSensorEvent(accelEvent)
        Logger.d("Context", "Walking: $isWalking")
        return BufferContext(
            isWalking = isWalking,
            signalLevel = currentSignalLevel,
            playbackState = currentPlaybackState,
        )
    }
}