// File: app/src/main/java/com/samsung/innovatex/buffermind/sensors/SensorManagerWrapper.kt

package com.samsung.innovatex.buffermind.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.samsung.innovatex.buffermind.util.Logger

class SensorManagerWrapper(
    private val context: Context,
) {

    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val accelerometer: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble())
            Logger.d("Sensor", "Accel: $x, $y, $z, |a|=$magnitude")
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Logger.d("Sensor", "Accuracy: $accuracy")
        }
    }

    fun startListening() {
        accelerometer?.let { sensor ->
            sensorManager.registerListener(
                listener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Logger.d("Sensor", "Accelerometer listening started")
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(listener)
        Logger.d("Sensor", "Accelerometer listening stopped")
    }
}