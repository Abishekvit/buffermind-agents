// File: app/src/main/java/com/samsung/innovatex/buffermind/service/BufferMindForegroundService.kt

package com.samsung.innovatex.buffermind.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.samsung.innovatex.buffermind.util.Logger

class BufferMindForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        Logger.d("Service", "Foreground service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.d("Service", "Foreground service started (startId=$startId)")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}