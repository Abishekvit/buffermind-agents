package com.samsung.innovatex.buffermind.ai
import com.samsung.innovatex.buffermind.domain.CacheAction
import com.samsung.innovatex.buffermind.domain.CacheState
import com.samsung.innovatex.buffermind.domain.CacheReward

// File: ai/CachePolicyAgent.kt

class CachePolicyAgent(
    private val replayBuffer: ReplayBuffer = ReplayBuffer(),
    private val epsilon: Float = 0.1f,
    private val gamma: Float = 0.95f,
    private val learningRate: Float = 0.01f,
) {

    private val valueTable = mutableMapOf<String, Float>()  // State.toString → estimated value

    fun chooseAction(state: CacheState): CacheAction {
        if (Math.random().toFloat() < epsilon) {
            // Exploration
            return CacheAction.entries.random()
        }

        // Exploitation: pick action with highest estimated value
        return CacheAction.entries.maxByOrNull { action ->
            val value = estimateValue(state, action)
            value
        } ?: CacheAction.KeepInCache
    }

    fun update(state: CacheState, action: CacheAction, reward: CacheReward, nextState: CacheState) {
        val stateKey = state.hashCode().toString()
        val qOld = valueTable.getOrDefault(stateKey, 0f)

        val nextQ = nextState.value()
        val qNew = qOld + learningRate * (reward.value + gamma * nextQ - qOld)

        valueTable[stateKey] = qNew
        // add to replay buffer
        replayBuffer.add(state, action, reward, nextState)
    }

    private fun CacheState.value(): Float {
        // Pseudo‑state value: you can add a simple formula
        val cacheScore = (1 - cacheOccupancyPercent) * 0.3f
        val latencyScore = (1000f - avgLatencyMs) / 1000f * 0.3f
        val hitScore = cacheHitRate * 0.4f
        return cacheScore + latencyScore + hitScore
    }

    fun estimateValue(state: CacheState, action: CacheAction): Float {
        val stateKey = state.hashCode().toString()
        return valueTable.getOrDefault(stateKey, 0f)
    }

    fun trainFromReplay(batchSize: Int = 32) {
        val batch = replayBuffer.sample(batchSize)
        batch.forEach { exp ->
            update(exp.state, exp.action, exp.reward, exp.nextState)
        }
    }
}