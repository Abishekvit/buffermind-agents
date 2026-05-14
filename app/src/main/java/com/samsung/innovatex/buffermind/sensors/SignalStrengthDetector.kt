// File: app/src/main/java/com/samsung/innovatex/buffermind/sensors/SignalStrengthDetector.kt

package com.samsung.innovatex.buffermind.sensors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager

sealed class SignalLevel {
    object Unknown : SignalLevel()
    object Weak : SignalLevel()
    object Fair : SignalLevel()
    object Strong : SignalLevel()
}

class SignalStrengthDetector(
    private val context: Context,
    private val onChange: (SignalLevel) -> Unit
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val telephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: android.net.Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val level = when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                    determineWifiLevel()
                else ->
                    determineCellularLevel()
            }
            onChange(level)
        }
    }

    fun startListening() {
        context.registerReceiver(signalReceiver, IntentFilter().apply {
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        })
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun stopListening() {
        context.unregisterReceiver(signalReceiver)
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private val signalReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = when {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ->
                    determineWifiLevel()
                else ->
                    determineCellularLevel()
            }
            onChange(level)
        }
    }

    private fun determineWifiLevel(): SignalLevel {
        // Skip full RSSI parsing here; for demo we can stub
        return SignalLevel.Fair // stub; in reality inspect wifi signal from Intent extras
    }

    private fun determineCellularLevel(): SignalLevel {
        telephonyManager?.signalStrength?.let { strength ->
            // strength.level is -1=unknown, 0–4 from Android API
            return when (strength.level) {
                -1 -> SignalLevel.Unknown
                0, 1 -> SignalLevel.Weak
                2 -> SignalLevel.Fair
                3, 4 -> SignalLevel.Strong
            }
        }
        return SignalLevel.Unknown
    }
}