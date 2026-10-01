package com.example.models

enum class IncidentType(val title: String, val subtitle: String, val iconCode: String) {
    TRAFICO_SEVERO("Tráfico Severo", "Mercado Modelo / Balta", "traffic"),
    ACCIDENTE_VIAL("Accidente Vial", "Colisión o atropello", "car_crash"),
    VIA_BLOQUEADA("Vía Bloqueada", "Obras o mantenimiento", "construction"),
    FALLA_MECANICA("Falla Mecánica", "Unidad averiada en ruta", "build"),
    SEGURIDAD("Seguridad", "Asalto o actividad sospechosa", "security"),
    SEMAFORO_APAGADO("Semáforo Apagado", "Cruce sin señalización", "warning")
}

enum class IncidentSeverity(val title: String, val description: String) {
    BAJA("Baja", "Informativa"),
    MEDIA("Media", "Retraso"),
    CRITICA("Crítica", "Inmediata")
}

enum class IncidentStatus(val label: String) {
    ACTIVA("Activa"),
    EN_REVISION("En revisión"),
    RESUELTA("Resuelta"),
    EXPIRADA("Expirada")
}

data class IncidentReport(
    val id: String,
    val usuarioNombre: String,
    val direccion: String = "Av. José Balta / Av. Bolognesi (Chiclayo Centro)",
    val tipo: IncidentType,
    val gravedad: IncidentSeverity,
    val descripcion: String,
    val tieneFoto: Boolean = false,
    val tieneAudio: Boolean = false,
    val alertarFlotaEnRuta: Boolean = true,
    val fecha: String,
    val hora: String,
    val estado: IncidentStatus = IncidentStatus.ACTIVA
)
