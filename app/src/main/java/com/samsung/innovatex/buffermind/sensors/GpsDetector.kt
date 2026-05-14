// File: app/src/main/java/com/samsung/innovatex/buffermind/sensors/GpsDetector.kt

package com.samsung.innovatex.buffermind.sensors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.samsung.innovatex.buffermind.util.Logger

class GpsDetector(
    private val context: Context,
    private val onMovementUpdate: (Boolean) -> Unit
) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            lastLocation?.let {
                val speed = it.speed // m/s
                val isMoving = speed > 1.0f  // walking speed ~1.4 m/s
                onMovementUpdate(isMoving)
                Logger.d("GPS", "Speed: ${speed}m/s, Moving: $isMoving")
            }
        }
    }

    fun startListening() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Logger.e("GPS", "Location permission denied")
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_LOW_POWER, 5000L
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopListening() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}