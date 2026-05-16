package com.samsung.innovatex.buffermind.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object MemoryRetrofitClient {

    private const val BASE_URL =
        "http://10.0.2.2:8000/"
//replace with laptop ip if actually using mobile
    val api: BufferMindMemoryApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                MoshiConverterFactory.create()
            )
            .build()
            .create(BufferMindMemoryApi::class.java)
    }
}