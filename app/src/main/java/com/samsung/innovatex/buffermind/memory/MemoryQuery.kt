package com.samsung.innovatex.buffermind.memory

data class MemoryQuery(
    val queryText: String,
    val nResults: Int = 3,
)