package com.example.models

enum class UserRole {
    PASAJERO,
    CONDUCTOR,
    GESTOR_LINEA
}

enum class AuthProvider {
    GOOGLE,
    FACEBOOK,
    PHONE,
    EMAIL
}

data class User(
    val id: String,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val telefono: String,
    val correo: String,
    val fechaNacimiento: String = "",
    val rol: UserRole = UserRole.PASAJERO,
    val calificacion: Double = 4.96,
    val totalViajes: Int = 24,
    val esVip: Boolean = true,
    val authProvider: AuthProvider = AuthProvider.GOOGLE,
    val photoUrl: String? = null
) {
    val nombreCompleto: String
        get() = "$nombres $apellidos".trim()

    val dniEnmascarado: String
        get() = if (dni.length >= 8) "${dni.take(2)}****${dni.takeLast(2)}" else dni
}
