// File: app/src/main/java/com/samsung/innovatex/buffermind/domain/PredictiveContextEngine.kt

package com.samsung.innovatex.buffermind.domain

import com.samsung.innovatex.buffermind.sensors.FusedContext
import com.samsung.innovatex.buffermind.sensors.SensorFusionManager
import com.samsung.innovatex.buffermind.util.Logger

interface PredictionListener {
    fun onBufferPredicted(risk: Float)
    fun onBufferStarted()
    fun onSeamlessPlayback()
}

class PredictiveContextEngine(
    private val sensorFusion: SensorFusionManager,
    private val listener: BufferTriggerListener
) {

    fun processContext(fusedContext: FusedContext) {
        val risk = fusedContext.riskScore

        // "AI" prediction logic (fake LSTM for demo)
        val predictedDisconnect = risk > 0.6f

        if (predictedDisconnect) {
            Logger.d("PredictiveEngine", "PREDICTED DISCONNECT (risk: ${String.format("%.2f", risk)})")
            listener.onBufferPredicted(risk)
        }
    }
}