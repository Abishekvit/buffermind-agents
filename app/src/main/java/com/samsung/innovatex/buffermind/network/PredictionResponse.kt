package com.samsung.innovatex.buffermind.network

data class PredictionResponse(
    val disconnect_probability: Float,
    val confidence: Float,
    val should_buffer: Boolean,
    val buffer_minutes: Int,
    val reason: String,
)