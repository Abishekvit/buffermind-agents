package com.samsung.innovatex.buffermind.domain
import com.samsung.innovatex.buffermind.ai.BufferMindTfLite
import com.samsung.innovatex.buffermind.ai.PredictionSmoother
import com.samsung.innovatex.buffermind.sensors.SignalLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.samsung.innovatex.buffermind.network.PredictionRequest
import com.samsung.innovatex.buffermind.network.RetrofitClient
private val smoother = PredictionSmoother()
interface BufferTriggerListener {
    fun onPredictiveBufferNeeded(
        disconnectProbability: Float,
        confidence: Float,
        bufferMinutes: Int,
    )
    fun onBufferPredicted(risk: Float)

    fun onBufferStarted()

    fun onSeamlessPlayback()
}

class BufferTriggerManager(
    private val listener: BufferTriggerListener,
    private val tfLite: BufferMindTfLite,

) {
    fun triggerPredictiveBuffer(context: BufferContext) {
        val result = tfLite.predict(context)
        if (result.shouldBuffer) {
            listener.onPredictiveBufferNeeded(
                result.disconnectProbability,
                result.confidence,
                result.bufferMinutes,
            )
        }
    }
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
                val smoothed =
                    smoother.smooth(
                        response.disconnect_probability
                    )
                if (response.should_buffer) {

                    listener.onBufferPredicted(
                        smoothed.smoothedProb
                    )

                    listener.onBufferStarted()
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}