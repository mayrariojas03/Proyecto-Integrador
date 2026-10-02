package com.example.data.remote

import retrofit2.http.GET

interface SmartChiclayoApi {

    @GET("api/usuarios")
    suspend fun listarUsuarios(): List<UsuarioResponse>
}