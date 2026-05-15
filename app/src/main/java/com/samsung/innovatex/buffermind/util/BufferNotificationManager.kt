// File: app/src/main/java/com/samsung/innovatex/buffermind/util/BufferNotificationManager.kt

package com.samsung.innovatex.buffermind.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.samsung.innovatex.buffermind.R
import com.samsung.innovatex.buffermind.service.BufferMindService

class BufferNotificationManager(private val context: Context) {

    private val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "buffermind_channel"
        const val FOREGROUND_CHANNEL_ID = "buffermind_foreground"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "BufferMind Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General BufferMind notifications"
            }
            val foregroundChannel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                "BufferMind Foreground",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Buffering / foreground service"
            }

            manager.createNotificationChannel(channel)
            manager.createNotificationChannel(foregroundChannel)
        }
    }

    fun showBasicNotification(
        title: String,
        text: String,
        intent: PendingIntent? = null
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)  // add an icon
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(intent)

        manager.notify(NOTIFICATION_ID, builder.build())
    }

    fun showBufferingForegroundNotification() {
        val intent = Intent(context, BufferMindService::class.java)
        val pendingIntent = PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("BufferMind Agent")
            .setContentText("Buffering 30min ahead...")
            .setSubText("Adaptive memory optimization active")
            .setOngoing(true)
            // .setForegroundServiceType(ForegroundServiceType.MEDIA_PLAYBACK)
            .setContentIntent(pendingIntent)

        val notification = builder.build()
        manager.notify(1002, notification)
    }

    fun cancelBufferingNotification() {
        manager.cancel(1002)
    }
}