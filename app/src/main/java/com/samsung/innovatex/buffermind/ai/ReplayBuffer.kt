package com.samsung.innovatex.buffermind.ai
import com.samsung.innovatex.buffermind.domain.CacheAction
import com.samsung.innovatex.buffermind.domain.CacheState
import com.samsung.innovatex.buffermind.domain.CacheReward

// File: ai/ReplayBuffer.kt

data class CacheExperience(
    val state: CacheState,
    val action: CacheAction,
    val reward: CacheReward,
    val nextState: CacheState,
)

class ReplayBuffer(
    val capacity: Int = 10000,
) {

    private val buffer = mutableListOf<CacheExperience>()

    fun add(state: CacheState, action: CacheAction, reward: CacheReward, nextState: CacheState) {
        buffer.add(CacheExperience(state, action, reward, nextState))
        if (buffer.size > capacity) {
            buffer.removeAt(0)
        }
    }

    fun sample(batchSize: Int): List<CacheExperience> {
        if (buffer.size < batchSize) return emptyList()
        return (0 until batchSize).map {
            buffer.random()
        }
    }

    fun clear() {
        buffer.clear()
    }
}