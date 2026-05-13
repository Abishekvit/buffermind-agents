// File: app/src/main/java/com/samsung/innovatex/buffermind/data/InMemoryCache.kt

package com.samsung.innovatex.buffermind.data

import java.io.File

class InMemoryCache {
    private val cache = mutableMapOf<String, File>()

    fun put(id: String, file: File) {
        cache[id] = file
    }

    fun get(id: String): File? = cache[id]

    fun size(): Int = cache.size

    fun clear() {
        cache.clear()
    }
}