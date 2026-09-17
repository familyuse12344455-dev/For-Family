package com.example.data.remote.supabase

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val apiKey = SupabaseConfig.anonKey
        val userToken = SupabaseSessionManager.getAccessToken()
        // If a real user session is active, use their JWT token; otherwise use anon key
        val bearerToken = if (!userToken.isNullOrBlank()) userToken else apiKey

        val requestBuilder = original.newBuilder()
            .header("apikey", apiKey)
            .header("Authorization", "Bearer $bearerToken")

        if (original.header("Accept") == null) {
            requestBuilder.header("Accept", "application/json")
        }
        if (original.header("Content-Type") == null) {
            requestBuilder.header("Content-Type", "application/json")
        }

        chain.proceed(requestBuilder.build())
    }

    val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    val api: SupabaseApiService by lazy {
        val baseUrl = if (SupabaseConfig.url.endsWith("/")) {
            SupabaseConfig.url
        } else {
            "${SupabaseConfig.url}/"
        }

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabaseApiService::class.java)
    }
}
