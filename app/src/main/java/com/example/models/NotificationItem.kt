package com.example.models

enum class NotificationCategory(val title: String) {
    VIAJES("Viajes"),
    SEGURIDAD("Seguridad"),
    LINEA_TAXI("Línea de taxi"),
    PAGOS("Pagos"),
    SISTEMA("Sistema")
}

data class NotificationItem(
    val id: String,
    val categoria: NotificationCategory,
    val titulo: String,
    val mensaje: String,
    val fecha: String,
    val hora: String,
    val leida: Boolean = false,
    val accionDestino: String? = null
)
