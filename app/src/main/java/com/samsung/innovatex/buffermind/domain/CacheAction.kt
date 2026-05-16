package com.samsung.innovatex.buffermind.domain

// File: domain/CacheAction.kt

enum class CacheAction(
    val actionId: String,
    val description: String
) {
    KeepInCache("keep_in_cache", "Do not evict"),
    EvictTrack("evict", "Remove from cache"),
    PrefetchNextTrack("prefetch_next", "Buffer next track"),
    ExpandBuffer("expand_buffer", "Buffer more seconds"),
    ReduceBuffer("reduce_buffer", "Buffer fewer seconds"),
}