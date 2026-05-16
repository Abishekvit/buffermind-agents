package com.samsung.innovatex.buffermind.memory

import com.samsung.innovatex.buffermind.network.MemoryRetrofitClient

class MemoryManager {

    suspend fun storeEpisode(
        episode: EpisodeMemory
    ) {

        try {

            MemoryRetrofitClient.api
                .storeMemory(episode)

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    suspend fun querySimilarMemories(
        query: String
    ): List<MemoryResult> {

        return try {

            MemoryRetrofitClient.api.queryMemory(
                MemoryQuery(query)
            )

        } catch (e: Exception) {

            e.printStackTrace()
            emptyList()
        }
    }
}