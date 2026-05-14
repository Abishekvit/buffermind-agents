// File: app/src/main/java/com/samsung/innovatex/buffermind/sensors/WifiSignalDetector.kt

package com.samsung.innovatex.buffermind.sensors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import com.samsung.innovatex.buffermind.util.Logger

sealed class SignalQuality {
    object Strong : SignalQuality()
    object Fair : SignalQuality()
    object Weak : SignalQuality()
    object Unknown : SignalQuality()
}

class WifiSignalDetector(
    private val context: Context,
    private val onSignalChange: (SignalQuality) -> Unit
) {

    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun startMonitoring() {
        if (!wifiManager.isWifiEnabled) {
            onSignalChange(SignalQuality.Unknown)
            return
        }

        val wifiInfo = wifiManager.connectionInfo
        val rssi = wifiInfo.rssi  // -100 to -55 dBm
        val quality = when {
            rssi <= -80 -> SignalQuality.Weak
            rssi <= -60 -> SignalQuality.Fair
            else -> SignalQuality.Strong
        }
        onSignalChange(quality)
        Logger.d("Wifi", "RSSI: ${rssi}dBm -> $quality")
    }

    fun getCurrentSignal(): SignalQuality {
        val wifiInfo = wifiManager.connectionInfo
        val rssi = wifiInfo.rssi
        return when {
            rssi <= -80 -> SignalQuality.Weak
            rssi <= -60 -> SignalQuality.Fair
            else -> SignalQuality.Strong
        }
    }
}