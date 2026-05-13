// File: app/src/main/java/com/samsung/innovatex/buffermind/BufferMindApplication.kt

package com.samsung.innovatex.buffermind

import android.app.Application
import com.samsung.innovatex.buffermind.util.Logger

class BufferMindApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.d("App", "BufferMindApplication started")
    }
}