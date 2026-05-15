package com.samsung.innovatex.buffermind.domain

import com.samsung.innovatex.buffermind.sensors.SignalLevel

interface BufferTriggerListener {

    fun onBufferPredicted(risk: Float)

    fun onBufferStarted()

    fun onSeamlessPlayback()
}

class BufferTriggerManager(
    private val listener: BufferTriggerListener,
) {

    fun considerBuffer(
        context: BufferContext,
        isPlayingRepeated: Boolean,
    ) {

        var risk = 0f

        if (context.isWalking) {
            risk += 0.4f
        }

        if (context.signalLevel == SignalLevel.Weak) {
            risk += 0.4f
        }

        if (isPlayingRepeated) {
            risk += 0.2f
        }

        if (risk >= 0.7f) {

            listener.onBufferPredicted(risk)

            listener.onBufferStarted()
        }
    }
}