package com.example.models

enum class TripType(val displayName: String, val badge: String, val iconName: String) {
    TAXI_SMART("Taxi Smart", "RÁPIDO", "local_taxi"),
    MOTOTAXI("Mototaxi", "ECONÓMICO", "two_wheeler"),
    COLECTIVO("Colectivo", "RUTA FIJA", "group"),
    COMBI("Combi", "POPULAR", "directions_bus"),
    ENVIO_EXPRESS("Envío Express", "ENTREGADO", "inventory_2"),
    CHICLAYO_A_CIUDADES("Chiclayo a Ciudades", "INTERURBANO", "public")
}

enum class TripStatus(val label: String) {
    SOLICITADO("Solicitado"),
    BUSCANDO_CONDUCTOR("Buscando conductor..."),
    CONDUCTOR_ASIGNADO("Conductor asignado"),
    EN_CAMINO_RECOJO("En camino al recojo"),
    LLEGADA_RECOJO("He llegado al punto"),
    EN_CURSO("Viaje en curso"),
    COMPLETADO("Completado"),
    CANCELADO("Cancelado")
}

data class Trip(
    val id: String,
    val codigoViaje: String,
    val tipoServicio: TripType,
    val origen: String,
    val origenDetalle: String = "",
    val destino: String,
    val destinoDetalle: String = "",
    val fecha: String,
    val hora: String,
    val precio: Double,
    val distanciaKm: Double,
    val duracionMin: Int,
    val metodoPago: String,
    val estado: TripStatus = TripStatus.COMPLETADO,
    val nombreConductor: String = "Carlos Mendoza Q.",
    val vehiculoConductor: String = "Toyota Yaris • Blanco",
    val placaConductor: String = "M3B-120",
    val calificacionConductor: Double = 4.9,
    val fotoConductor: String? = null,
    val calificacionPasajero: Int? = 5
)
