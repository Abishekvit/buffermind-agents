package com.samsung.innovatex.buffermind.network

import com.samsung.innovatex.buffermind.memory.*
import retrofit2.http.Body
import retrofit2.http.POST

interface BufferMindMemoryApi {

    @POST("/memory/store")
    suspend fun storeMemory(
        @Body episode: EpisodeMemory
    ): Map<String, Any>

    @POST("/memory/query")
    suspend fun queryMemory(
        @Body query: MemoryQuery
    ): List<MemoryResult>
}