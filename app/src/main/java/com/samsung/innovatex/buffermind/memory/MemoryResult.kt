package com.samsung.innovatex.buffermind.memory

data class MemoryResult(
    val semanticScore: Float,
    val episodeId: String,
    val playbackContext: String,
    val cacheSuccessRate: Float,
    val recommendedBufferStrategy: String,
)