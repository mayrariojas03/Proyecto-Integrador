package com.example.models

enum class DriverStatus(val label: String) {
    DESCONECTADO("Desconectado"),
    DISPONIBLE("Disponible"),
    OCUPADO("Ocupado")
}

data class DriverProfile(
    val id: String,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val telefono: String,
    val correo: String,
    val numeroLicencia: String,
    val categoriaLicencia: String,
    val fechaVencimiento: String,
    val estadoVerificacion: String = "VERIFICADO",
    val estadoOperativo: DriverStatus = DriverStatus.DISPONIBLE,
    val calificacion: Double = 4.9,
    val totalViajesRealizados: Int = 184,
    val vehiculoMarcaModelo: String = "Toyota Yaris 2022",
    val placa: String = "M3B-120"
)

data class IncomingTripRequest(
    val id: String,
    val pasajeroNombre: String,
    val pasajeroCalificacion: Double,
    val origen: String,
    val destino: String,
    val distanciaKm: Double,
    val duracionMin: Int,
    val tarifaOfrecida: Double,
    val metodoPago: String
)
