// File: app/src/main/java/com/samsung/innovatex/buffermind/util/Logger.kt

package com.samsung.innovatex.buffermind.util

import android.util.Log

object Logger {
    private const val TAG = "BufferMind"

    fun d(tag: String, message: String) {
        Log.d("$TAG:$tag", message)
    }

    fun e(tag: String, message: String) {
        Log.e("$TAG:$tag", message)
    }

    fun i(tag: String, message: String) {
        Log.i("$TAG:$tag", message)
    }
}