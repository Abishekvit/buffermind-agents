// File: app/src/main/java/com/samsung/innovatex/buffermind/data/FakeBufferCache.kt

package com.samsung.innovatex.buffermind.data

import java.io.File

class FakeBufferCache {
    private val bufferedSegments = mutableMapOf<String, String>()

    fun preloadSegment(trackId: String, durationMinutes: Int) {
        bufferedSegments[trackId] = "FAKE_${durationMinutes}min"
    }

    fun hasBuffer(trackId: String): Boolean = bufferedSegments.containsKey(trackId)

    fun getBuffer(trackId: String): String? = bufferedSegments[trackId]

    fun clear() {
        bufferedSegments.clear()
    }
}