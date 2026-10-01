package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [
        Index(value = ["numero_documento"], unique = true),
        Index(value = ["telefono"], unique = true),
        Index(value = ["correo"], unique = true)
    ]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id_usuario: Long = 0,
    val nombres: String,
    val apellidos: String,
    val tipo_documento: String = "DNI",
    val numero_documento: String,
    val telefono: String,
    val correo: String,
    val password_hash: String? = null,
    val foto_perfil: String? = null,
    val tipo_usuario: String = "PASAJERO",
    val estado: String = "ACTIVO",
    val telefono_verificado: Boolean = true,
    val correo_verificado: Boolean = true,
    val fecha_registro: String = "",
    val fecha_actualizacion: String = ""
)

@Entity(
    tableName = "perfiles_usuario",
    indices = [Index(value = ["id_usuario"], unique = true)]
)
data class PerfilUsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id_perfil: Long = 0,
    val id_usuario: Long,
    val fecha_nacimiento: String? = null,
    val direccion: String? = "Chiclayo Centro",
    val ciudad: String = "Chiclayo",
    val departamento: String = "Lambayeque"
)

@Entity(
    tableName = "configuraciones_usuario",
    indices = [Index(value = ["id_usuario"], unique = true)]
)
data class ConfiguracionUsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id_configuracion: Long = 0,
    val id_usuario: Long,
    val tema: String = "OSCURO",
    val notificaciones_viajes: Boolean = true,
    val notificaciones_seguridad: Boolean = true,
    val notificaciones_sistema: Boolean = true,
    val compartir_ubicacion: Boolean = true
)

@Entity(
    tableName = "pasajeros",
    indices = [Index(value = ["id_usuario"], unique = true)]
)
data class PasajeroEntity(
    @PrimaryKey(autoGenerate = true)
    val id_pasajero: Long = 0,
    val id_usuario: Long,
    val promedio_calificacion: Double = 0.0,
    val total_viajes: Int = 0
)

@Entity(
    tableName = "conductores",
    indices = [
        Index(value = ["id_usuario"], unique = true),
        Index(value = ["numero_licencia"], unique = true)
    ]
)
data class ConductorEntity(
    @PrimaryKey(autoGenerate = true)
    val id_conductor: Long = 0,
    val id_usuario: Long,
    val numero_licencia: String,
    val categoria_licencia: String = "A-IIa",
    val vencimiento_licencia: String = "2030-12-31",
    val estado_validacion: String = "VERIFICADO",
    val estado_servicio: String = "DISPONIBLE",
    val promedio_calificacion: Double = 5.0,
    val total_viajes: Int = 0
)

@Entity(tableName = "viajes_registrados")
data class ViajeEntity(
    @PrimaryKey(autoGenerate = true)
    val id_viaje: Long = 0,
    val codigo_viaje: String,
    val id_usuario: Long,
    val origen: String,
    val destino: String,
    val tipo_servicio: String,
    val tarifa_final: Double,
    val distancia_km: Double,
    val duracion_minutos: Int,
    val metodo_pago: String,
    val estado: String = "COMPLETADO",
    val nombre_conductor: String,
    val vehiculo_conductor: String,
    val placa_conductor: String,
    val fecha: String,
    val hora: String
)
