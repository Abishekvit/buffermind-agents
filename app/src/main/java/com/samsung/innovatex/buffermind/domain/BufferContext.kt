// File: app/src/main/java/com/samsung/innovatex/buffermind/domain/BufferContext.kt

package com.samsung.innovatex.buffermind.domain

import com.samsung.innovatex.buffermind.sensors.SignalLevel
import com.samsung.innovatex.buffermind.feature.player.PlayerState

// In domain/BufferContext.kt (add field)

data class BufferContext(
    val isWalking: Boolean,
    val signalLevel: SignalLevel,
    val playbackState: PlayerState,
    val isPlayingRepeated: Boolean,
    val playbackDurationMinutes: Float,
)