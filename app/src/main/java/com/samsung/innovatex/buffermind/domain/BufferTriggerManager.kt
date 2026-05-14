// File: app/src/main/java/com/samsung/innovatex/buffermind/domain/BufferTriggerManager.kt

package com.samsung.innovatex.buffermind.domain

import com.samsung.innovatex.buffermind.sensors.SignalLevel
import com.samsung.innovatex.buffermind.feature.player.PlayerState
import com.samsung.innovatex.buffermind.util.Logger

interface BufferTriggerListener {
    fun onPredictiveBufferNeeded()
}

class BufferTriggerManager(
    private val listener: BufferTriggerListener,
) {

    private var lastWasTriggered = false

    fun considerBuffer(context: BufferContext) {
        val shouldTrigger =
            context.isWalking &&
                    (context.signalLevel == SignalLevel.Weak ||
                            context.signalLevel == SignalLevel.Unknown) &&
                    context.playbackState == PlayerState.Playing

        if (shouldTrigger && !lastWasTriggered) {
            Logger.d("BufferTrigger", "Predictive buffer triggered: ${context.signalLevel}")
            listener.onPredictiveBufferNeeded()
        } else if (!shouldTrigger && lastWasTriggered) {
            Logger.d("BufferTrigger", "Buffer context no longer active")
        }

        lastWasTriggered = shouldTrigger
    }
}