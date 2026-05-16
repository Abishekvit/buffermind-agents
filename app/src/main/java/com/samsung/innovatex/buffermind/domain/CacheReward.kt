package com.samsung.innovatex.buffermind.domain

// File: domain/CacheReward.kt

data class CacheReward(
    val value: Float,
    val breakdown: Map<String, Float>,
)

class CacheRewardCalculator {

    fun calculate(
        didCacheHit: Boolean,
        didPlaybackInterrupt: Boolean,
        didBufferingOvershoot: Boolean,
        memoryOveruse: Boolean,
        latencyMs: Float,
        isPrefetchSuccessful: Boolean,
    ): CacheReward {
        var reward = 0f

        val components = mutableMapOf<String, Float>()

        val baseLatency = 200f

        // + cache hit
        if (didCacheHit) {
            val hitBonus = 1.0f
            reward += hitBonus
            components["cache_hit"] = hitBonus
        }

        // - playback interruption
        if (didPlaybackInterrupt) {
            val interruptionPenalty = -2.0f
            reward += interruptionPenalty
            components["playback_interrupt"] = interruptionPenalty
        }

        // + successful prefetch
        if (isPrefetchSuccessful) {
            val prefetchBonus = 0.8f
            reward += prefetchBonus
            components["prefetch_success"] = prefetchBonus
        }

        // - unnecessary buffering / memory overuse
        if (memoryOveruse || didBufferingOvershoot) {
            val overusePenalty = -0.5f
            reward += overusePenalty
            components["memory_overuse"] = overusePenalty
        }

        // + latency reduction
        val latencyBonus = (baseLatency - latencyMs) / baseLatency
        if (latencyBonus > 0) {
            reward += latencyBonus
            components["latency_reduction"] = latencyBonus
        } else {
            val penalty = 0.3f
            reward -= penalty
            components["high_latency"] = -penalty
        }

        return CacheReward(reward, components)
    }
}