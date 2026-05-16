package com.samsung.innovatex.buffermind.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.100:8000/")   // laptop IP, not 10.0.2.2
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
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