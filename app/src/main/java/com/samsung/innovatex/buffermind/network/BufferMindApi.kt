package com.samsung.innovatex.buffermind.network

import retrofit2.http.Body
import retrofit2.http.POST

interface BufferMindApi {

    @POST("/predict")
    suspend fun predict(
        @Body request: PredictionRequest
    ): PredictionResponse
}