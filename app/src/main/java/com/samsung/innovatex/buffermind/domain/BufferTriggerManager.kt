package com.samsung.innovatex.buffermind.domain

import com.samsung.innovatex.buffermind.sensors.SignalLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.samsung.innovatex.buffermind.network.PredictionRequest
import com.samsung.innovatex.buffermind.network.RetrofitClient
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
        CoroutineScope(Dispatchers.IO).launch {

            try {

                val request = PredictionRequest(
                    walking = context.isWalking,
                    weak_signal =
                        context.signalLevel == SignalLevel.Weak,
                    repeated_playback = isPlayingRepeated,
                    gps_moving = true,
                    playback_duration_minutes = 15f
                )

                val response =
                    RetrofitClient.api.predict(request)

                if (response.should_buffer) {

                    listener.onBufferPredicted(
                        response.disconnect_probability
                    )

                    listener.onBufferStarted()
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}