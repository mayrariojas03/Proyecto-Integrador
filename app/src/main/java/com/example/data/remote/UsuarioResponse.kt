package com.example.data.remote

data class UsuarioResponse(
    val idUsuario: Long,
    val nombres: String,
    val apellidos: String,
    val tipoDocumento: String?,
    val numeroDocumento: String,
    val telefono: String,
    val correo: String?,
    val fotoPerfil: String?,
    val tipoUsuario: String,
    val estado: String,
    val telefonoVerificado: Boolean,
    val correoVerificado: Boolean
)