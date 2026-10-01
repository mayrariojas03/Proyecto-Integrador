package com.example.models

data class ConcessionAccreditation(
    val razonSocial: String = "ET Chiclayo Norte S.A.",
    val ruc: String = "20601928471",
    val cargoSolicitante: String = "Gerente de Operaciones",
    val regimen: String = "Régimen regulatorio Lambayeque",
    val estadoValidacion: String = "Habilitado MTC/MPCH"
)

data class RouteLine(
    val id: String,
    val numero: String,
    val nombreOficial: String,
    val recorrido: String,
    val frecuencia: String,
    val cupoFlota: Int,
    val codigoMtc: String,
    val distanciaKm: Double,
    val esPrincipal: Boolean = false,
    val ramalesSecundarios: List<String> = emptyList()
)

data class VehicleUnit(
    val id: String,
    val placa: String,
    val numeroPadron: String,
    val categoria: String,
    val conductorAsignado: String,
    val estadoValidacion: String = "Validado MTC/MPCH"
)

data class LineDriver(
    val id: String,
    val nombres: String,
    val unidadPlaca: String,
    val canalPttActivo: Boolean = true,
    val telefonoWhatsapp: String = ""
)
