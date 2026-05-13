// File: app/src/main/java/com/samsung/innovatex/buffermind/sensors/WalkingDetector.kt

package com.samsung.innovatex.buffermind.sensors

import android.hardware.SensorEvent
import com.samsung.innovatex.buffermind.util.Logger

class WalkingDetector {

    fun onSensorEvent(event: SensorEvent): Boolean {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble())

        // Today: just log and return dummy
        Logger.d("Walking", "Raw |a| = $magnitude")
        return false // placeholder: will become real heuristic later
    }
}