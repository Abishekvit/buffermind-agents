package com.samsung.innovatex.buffermind.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {

    private const val BASE_URL =
        "http://192.168.1.100:8000/"

    val api: BufferMindApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                MoshiConverterFactory.create()
            )
            .build()
            .create(BufferMindApi::class.java)
    }
}