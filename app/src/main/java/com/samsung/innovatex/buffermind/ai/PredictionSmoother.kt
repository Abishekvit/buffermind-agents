package com.samsung.innovatex.buffermind.ai

data class SmoothedPrediction(
    val smoothedProb: Float,
    val smoothedConf: Float,
)

class PredictionSmoother {
    private var lastProb = 0.0f
    private val alpha = 0.2f

    fun smooth(newProb: Float): SmoothedPrediction {
        val smooth = lastProb * (1 - alpha) + newProb * alpha
        lastProb = smooth
        return SmoothedPrediction(smooth, 0.9f)
    }
}