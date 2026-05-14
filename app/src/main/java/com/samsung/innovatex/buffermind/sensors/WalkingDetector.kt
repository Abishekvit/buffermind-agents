// File: app/src/main/java/com/samsung/innovatex/buffermind/sensors/WalkingDetector.kt

package com.samsung.innovatex.buffermind.sensors

import android.hardware.SensorEvent

class WalkingDetector {

    private val threshold = 0.4   // g‑unit diff threshold for “step‑like”
    private val minSamples = 10   // minimum buffered samples before decision

    private var lastAccel: Float? = null
    private var walking = false
    private val history = mutableListOf<Float>()

    fun onSensorEvent(event: SensorEvent): Boolean {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val diff = lastAccel?.let { Math.abs(magnitude - it) } ?: 0f
        lastAccel = magnitude

        history.add(magnitude)
        if (history.size > 20) history.removeAt(0)

        // If very few samples or all almost flat -> no walking
        if (history.size < minSamples) return false

        // Count how many “spikes” cross our threshold
        val stepCount = history
            .windowed(2, 1)
            .count { (a, b) -> Math.abs(a - b) > threshold }

        walking = stepCount > 4  // 4+ strong changes in last 20 samples ≈ walking
        return walking
    }

    fun isWalking(): Boolean = walking
}