// File: app/src/main/java/com/samsung/innovatex/buffermind/data/PlayerRepository.kt

package com.samsung.innovatex.buffermind.data

import java.io.File
import com.samsung.innovatex.buffermind.util.Logger

class PlayerRepository {
    private val cache = InMemoryCache()

    fun getCachedSegment(trackId: String): File? {
        return cache.get(trackId)
    }

    fun cacheSegment(trackId: String, file: File) {
        cache.put(trackId, file)
        Logger.d("Cache", "Cached segment for $trackId (now ${cache.size()} items)")
    }
}