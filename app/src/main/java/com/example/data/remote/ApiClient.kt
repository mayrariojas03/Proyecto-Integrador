package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {

    // Token temporal en memoria. No se registra en logs ni se guarda en texto plano.
    @Volatile
    private var accessToken: String? = null

    fun establecerToken(token: String) {
        require(token.isNotBlank()) { "El JWT no puede estar vacio" }
        accessToken = token.trim()
    }

    fun cerrarSesion() {
        accessToken = null
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val token = accessToken
            val esBackendLocal = original.url.host == "10.0.2.2" &&
                original.url.port == 8080

            val request = if (!token.isNullOrBlank() && esBackendLocal) {
                original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                original
            }

            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: SmartChiclayoApi =
        retrofit.create(SmartChiclayoApi::class.java)
}
