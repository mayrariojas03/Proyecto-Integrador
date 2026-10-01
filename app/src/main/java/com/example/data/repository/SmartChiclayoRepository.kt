package com.example.data.repository

import com.example.data.local.dao.UsuarioDao
import com.example.data.local.dao.ViajeDao
import com.example.data.local.entities.ConfiguracionUsuarioEntity
import com.example.data.local.entities.PasajeroEntity
import com.example.data.local.entities.PerfilUsuarioEntity
import com.example.data.local.entities.UsuarioEntity
import com.example.data.local.entities.ViajeEntity
import com.example.models.AuthProvider
import com.example.models.Trip
import com.example.models.TripStatus
import com.example.models.TripType
import com.example.models.User
import com.example.models.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

sealed class RegisterResult {
    data class Success(val user: User) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

class SmartChiclayoRepository(
    private val usuarioDao: UsuarioDao,
    private val viajeDao: ViajeDao
) {

    suspend fun registerUser(
        nombres: String,
        apellidos: String,
        dni: String,
        telefono: String,
        correo: String,
        fechaNacimiento: String,
        authProvider: AuthProvider = AuthProvider.EMAIL
    ): RegisterResult = withContext(Dispatchers.IO) {
        val cleanPhone = telefono.replace(" ", "").replace("+51", "").trim()
        val altPhone = "+51 $cleanPhone"

        // 1. Validar que DNI no esté registrado
        if (usuarioDao.countByDocumento(dni) > 0) {
            return@withContext RegisterResult.Error("El número de documento (DNI) ya se encuentra registrado.")
        }

        // 2. Validar que teléfono no esté registrado
        if (usuarioDao.countByTelefono(cleanPhone, altPhone) > 0) {
            return@withContext RegisterResult.Error("El número de teléfono ya está asociado a otra cuenta.")
        }

        // 3. Validar que correo no esté registrado
        if (usuarioDao.countByCorreo(correo.trim()) > 0) {
            return@withContext RegisterResult.Error("El correo electrónico ya se encuentra registrado.")
        }

        // 4. Guardar en tabla usuarios (MySQL schema)
        val usuarioEntity = UsuarioEntity(
            nombres = nombres.trim(),
            apellidos = apellidos.trim(),
            tipo_documento = "DNI",
            numero_documento = dni.trim(),
            telefono = cleanPhone,
            correo = correo.trim(),
            tipo_usuario = "PASAJERO",
            estado = "ACTIVO",
            telefono_verificado = true,
            correo_verificado = true,
            fecha_registro = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        )

        val newId = usuarioDao.insertUsuario(usuarioEntity)

        // 5. Guardar en perfiles_usuario
        usuarioDao.insertPerfil(
            PerfilUsuarioEntity(
                id_usuario = newId,
                fecha_nacimiento = fechaNacimiento,
                direccion = "Chiclayo Centro",
                ciudad = "Chiclayo",
                departamento = "Lambayeque"
            )
        )

        // 6. Guardar en configuraciones_usuario
        usuarioDao.insertConfiguracion(
            ConfiguracionUsuarioEntity(
                id_usuario = newId,
                tema = "OSCURO",
                notificaciones_viajes = true,
                notificaciones_seguridad = true,
                notificaciones_sistema = true,
                compartir_ubicacion = true
            )
        )

        // 7. Guardar en pasajeros (con 0 viajes y 0.0 calificación inicial)
        usuarioDao.insertPasajero(
            PasajeroEntity(
                id_usuario = newId,
                promedio_calificacion = 0.0,
                total_viajes = 0
            )
        )

        val domainUser = User(
            id = newId.toString(),
            nombres = usuarioEntity.nombres,
            apellidos = usuarioEntity.apellidos,
            dni = usuarioEntity.numero_documento,
            telefono = usuarioEntity.telefono,
            correo = usuarioEntity.correo,
            fechaNacimiento = fechaNacimiento,
            rol = UserRole.PASAJERO,
            calificacion = 0.0,
            totalViajes = 0,
            esVip = false,
            authProvider = authProvider
        )

        RegisterResult.Success(domainUser)
    }

    suspend fun findUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        val entity = usuarioDao.getUsuarioByCorreo(email.trim()) ?: return@withContext null
        mapToDomainUser(entity)
    }

    suspend fun findUserByPhone(phone: String): User? = withContext(Dispatchers.IO) {
        val clean = phone.replace(" ", "").replace("+51", "").trim()
        val alt = "+51 $clean"
        val entity = usuarioDao.getUsuarioByTelefono(clean, alt) ?: return@withContext null
        mapToDomainUser(entity)
    }

    suspend fun saveTrip(trip: Trip, userId: Long) = withContext(Dispatchers.IO) {
        viajeDao.insertViaje(
            ViajeEntity(
                codigo_viaje = trip.codigoViaje,
                id_usuario = userId,
                origen = trip.origen,
                destino = trip.destino,
                tipo_servicio = trip.tipoServicio.displayName,
                tarifa_final = trip.precio,
                distancia_km = trip.distanciaKm,
                duracion_minutos = trip.duracionMin,
                metodo_pago = trip.metodoPago,
                estado = "COMPLETADO",
                nombre_conductor = trip.nombreConductor,
                vehiculo_conductor = trip.vehiculoConductor,
                placa_conductor = trip.placaConductor,
                fecha = trip.fecha,
                hora = trip.hora
            )
        )
    }

    fun getTripsForUser(userId: Long): Flow<List<Trip>> {
        return viajeDao.getViajesByUsuario(userId).map { list ->
            list.map { entity ->
                Trip(
                    id = entity.id_viaje.toString(),
                    codigoViaje = entity.codigo_viaje,
                    tipoServicio = when (entity.tipo_servicio) {
                        "Mototaxi" -> TripType.MOTOTAXI
                        "Colectivo" -> TripType.COLECTIVO
                        "Combi" -> TripType.COMBI
                        "Chiclayo a ciudades" -> TripType.CHICLAYO_A_CIUDADES
                        "Envío Express" -> TripType.ENVIO_EXPRESS
                        else -> TripType.TAXI_SMART
                    },
                    origen = entity.origen,
                    destino = entity.destino,
                    fecha = entity.fecha,
                    hora = entity.hora,
                    precio = entity.tarifa_final,
                    distanciaKm = entity.distancia_km,
                    duracionMin = entity.duracion_minutos,
                    metodoPago = entity.metodo_pago,
                    estado = TripStatus.COMPLETADO,
                    nombreConductor = entity.nombre_conductor,
                    vehiculoConductor = entity.vehiculo_conductor,
                    placaConductor = entity.placa_conductor,
                    calificacionConductor = 5.0
                )
            }
        }
    }

    private suspend fun mapToDomainUser(entity: UsuarioEntity): User {
        val perfil = usuarioDao.getPerfilByUsuarioId(entity.id_usuario)
        val pasajero = usuarioDao.getPasajeroByUsuarioId(entity.id_usuario)
        return User(
            id = entity.id_usuario.toString(),
            nombres = entity.nombres,
            apellidos = entity.apellidos,
            dni = entity.numero_documento,
            telefono = entity.telefono,
            correo = entity.correo,
            fechaNacimiento = perfil?.fecha_nacimiento ?: "",
            rol = when (entity.tipo_usuario) {
                "CONDUCTOR" -> UserRole.CONDUCTOR
                "GESTOR" -> UserRole.GESTOR_LINEA
                else -> UserRole.PASAJERO
            },
            calificacion = pasajero?.promedio_calificacion ?: 0.0,
            totalViajes = pasajero?.total_viajes ?: 0,
            esVip = (pasajero?.total_viajes ?: 0) >= 15,
            authProvider = AuthProvider.EMAIL
        )
    }
}
