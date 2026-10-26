package com.example.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SmartChiclayoApi {

    @GET("api/usuarios")
    suspend fun listarUsuarios(): List<UsuarioResponse>

    // Enviar registro al backend de Spring Boot / MySQL
    @POST("api/usuarios/registro")
    suspend fun registrarUsuario(
        @Body usuario: UsuarioRequest
    ): Response<RegistroResponse>

    // Validar login en el backend
    @POST("api/usuarios/login")
    suspend fun loginUsuario(
        @Body credenciales: Map<String, String>
    ): Response<RegistroResponse>

    // Actualizar usuario en el backend de Spring Boot y MySQL
    @retrofit2.http.PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @retrofit2.http.Path("id") id: Long,
        @retrofit2.http.Body campos: Map<String, String>
    ): retrofit2.Response<RegistroResponse>
}

// Modelos para la petición y respuesta
data class UsuarioRequest(
    val nombres: String,
    val apellidos: String,
    val tipoDocumento: String = "DNI",
    val numeroDocumento: String,
    val telefono: String,
    val correo: String,
    val passwordHash: String,
    val tipoUsuario: String = "PASAJERO",
    val fechaNacimiento: String = ""
)

data class RegistroResponse(
    val success: Boolean,
    val message: String,
    val usuario: UsuarioResponse? = null
)