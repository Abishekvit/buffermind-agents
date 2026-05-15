// File: app/src/main/java/com/samsung/innovatex/buffermind/service/BufferMindService.kt

package com.samsung.innovatex.buffermind.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.samsung.innovatex.buffermind.util.BufferNotificationManager

class BufferMindService : Service() {

    private val nm by lazy { BufferNotificationManager(this) }

    override fun onCreate() {
        super.onCreate()
        Log.d("BufferMind", "BufferMindService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BufferMind", "BufferMindService started (startId=$startId)")
        nm.showBufferingForegroundNotification()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        nm.cancelBufferingNotification()
    }
}