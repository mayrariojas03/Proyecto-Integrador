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

sealed class AuthLoginResult {
    data class Success(val user: User) : AuthLoginResult()
    data class Error(val message: String) : AuthLoginResult()
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
        password: String = "",
        authProvider: AuthProvider = AuthProvider.EMAIL
    ): RegisterResult = withContext(Dispatchers.IO) {
        val cleanPhone = telefono
            .replace(" ", "")
            .replace("+51", "")
            .trim()

        // 1. PRIMERO: ENVIAR A SPRING BOOT / MYSQL (Persistencia Real)
        try {
            val requestRemoto = com.example.data.remote.UsuarioRequest(
                nombres = nombres.trim(),
                apellidos = apellidos.trim(),
                numeroDocumento = dni.trim(),
                telefono = cleanPhone,
                correo = correo.trim(),
                passwordHash = password,
                tipoUsuario = "PASAJERO",
                fechaNacimiento = fechaParaMySQL(fechaNacimiento) // <-- ENVÍA LA FECHA A MYSQL
            )
            val apiResponse = com.example.data.remote.ApiClient.api.registrarUsuario(requestRemoto)
            if (!apiResponse.isSuccessful) {
                val errorBody = apiResponse.errorBody()?.string() ?: ""
                if (apiResponse.code() == 409 || errorBody.contains("DNI")) {
                    return@withContext RegisterResult.Error("El número de documento (DNI) ya se encuentra registrado.")
                }
            }
        } catch (e: Exception) {
            println("Aviso: Conexión remota MySQL no disponible, guardando en caché local: ${e.message}")
        }

        // 2. SEGUNDO: GUARDAR EN ROOM LOCAL PARA MODO OFFLINE
        val altPhone = "+51 $cleanPhone"
        if (usuarioDao.countByDocumento(dni) > 0) {
            return@withContext RegisterResult.Error(
                "El número de documento (DNI) ya se encuentra registrado."
            )
        }

        val usuarioEntity = UsuarioEntity(
            nombres = nombres.trim(),
            apellidos = apellidos.trim(),
            tipo_documento = "DNI",
            numero_documento = dni.trim(),
            telefono = cleanPhone,
            correo = correo.trim(),
            password_hash = password,
            tipo_usuario = "PASAJERO",
            estado = "ACTIVO",
            telefono_verificado = true,
            correo_verificado = true,
            fecha_registro = java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault()
            ).format(java.util.Date())
        )
        val newId = usuarioDao.insertUsuario(usuarioEntity)
        usuarioDao.insertPerfil(
            PerfilUsuarioEntity(
                id_usuario = newId,
                fecha_nacimiento = fechaNacimiento,
                direccion = "Chiclayo Centro",
                ciudad = "Chiclayo",
                departamento = "Lambayeque"
            )
        )
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

    suspend fun loginWithEmail(
        email: String,
        password: String
    ): AuthLoginResult = withContext(Dispatchers.IO) {

        val usuario = usuarioDao.getUsuarioByCorreo(email.trim())
            ?: return@withContext AuthLoginResult.Error(
                "No se encontró una cuenta con este correo electrónico."
            )

        if (usuario.password_hash.isNullOrBlank() ||
            usuario.password_hash != password
        ) {
            return@withContext AuthLoginResult.Error(
                "Contraseña incorrecta."
            )
        }

        AuthLoginResult.Success(
            mapToDomainUser(usuario)
        )
    }

    suspend fun loginWithPhone(
        phone: String,
        password: String
    ): AuthLoginResult = withContext(Dispatchers.IO) {

        val cleanPhone = phone
            .replace(" ", "")
            .replace("+51", "")
            .trim()

        val altPhone = "+51 $cleanPhone"

        val usuario = usuarioDao.getUsuarioByTelefono(
            cleanPhone,
            altPhone
        ) ?: return@withContext AuthLoginResult.Error(
            "No se encontró una cuenta con este número de celular."
        )

        if (usuario.password_hash.isNullOrBlank() ||
            usuario.password_hash != password
        ) {
            return@withContext AuthLoginResult.Error(
                "Contraseña incorrecta."
            )
        }

        AuthLoginResult.Success(
            mapToDomainUser(usuario)
        )
    }

    suspend fun findUserByEmail(email: String): User? =
        withContext(Dispatchers.IO) {
            // Sincroniza primero para traer cambios de phpMyAdmin
            sincronizarUsuariosDesdeMySQL()
            val entity = usuarioDao.getUsuarioByCorreo(email.trim())
                ?: return@withContext null
            mapToDomainUser(entity)
        }

    suspend fun findUserByPhone(phone: String): User? =
        withContext(Dispatchers.IO) {
            // Sincroniza primero para traer cambios de phpMyAdmin
            sincronizarUsuariosDesdeMySQL()
            val clean = phone.replace(" ", "").replace("+51", "").trim()
            val alt = "+51 $clean"
            val entity = usuarioDao.getUsuarioByTelefono(clean, alt)
                ?: return@withContext null
            mapToDomainUser(entity)
        }

    suspend fun saveTrip(
        trip: Trip,
        userId: Long
    ) = withContext(Dispatchers.IO) {

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

    private suspend fun mapToDomainUser(
        entity: UsuarioEntity
    ): User {

        val perfil =
            usuarioDao.getPerfilByUsuarioId(entity.id_usuario)

        val pasajero =
            usuarioDao.getPasajeroByUsuarioId(entity.id_usuario)

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

    // SINCRONIZAR: Descarga los cambios de MySQL y actualiza el teléfono
    suspend fun sincronizarUsuariosDesdeMySQL(): Boolean = withContext(Dispatchers.IO) {
        try {
            val usuariosRemotos = com.example.data.remote.ApiClient.api.listarUsuarios()
            for (remoto in usuariosRemotos) {
                val entity = UsuarioEntity(
                    id_usuario = remoto.idUsuario,
                    nombres = remoto.nombres,
                    apellidos = remoto.apellidos,
                    tipo_documento = remoto.tipoDocumento ?: "DNI",
                    numero_documento = remoto.numeroDocumento,
                    telefono = remoto.telefono,
                    correo = remoto.correo ?: "",
                    foto_perfil = remoto.fotoPerfil,
                    tipo_usuario = remoto.tipoUsuario,
                    estado = remoto.estado,
                    telefono_verificado = remoto.telefonoVerificado,
                    correo_verificado = remoto.correoVerificado
                )
                usuarioDao.insertOrUpdateUsuario(entity)

                // Guarda también la fecha de nacimiento en el perfil local:
                if (!remoto.fechaNacimiento.isNullOrBlank()) {
                    usuarioDao.insertPerfil(
                        com.example.data.local.entities.PerfilUsuarioEntity(
                            id_usuario = remoto.idUsuario,
                            fecha_nacimiento = fechaParaApp(remoto.fechaNacimiento),
                            direccion = "Chiclayo Centro",
                            ciudad = "Chiclayo",
                            departamento = "Lambayeque"
                        )
                    )
                }
            }
            true
        } catch (e: Exception) {
            println("Error al sincronizar desde MySQL: ${e.message}")
            false
        }
    }

    // ACTUALIZAR EN MYSQL Y, SOLO SI EL SERVIDOR CONFIRMA, EN ROOM LOCAL
    suspend fun actualizarPerfilUsuario(
        userId: Long,
        dni: String,
        telefono: String,
        correo: String,
        fechaNacimiento: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val campos = mapOf(
                "numeroDocumento" to dni.trim(),
                "telefono" to telefono.trim(),
                "correo" to correo.trim(),
                "fechaNacimiento" to fechaParaMySQL(fechaNacimiento)
            )
            val response = com.example.data.remote.ApiClient.api.actualizarUsuario(userId, campos)
            if (!response.isSuccessful) {
                // Registrar solo el mensaje público del backend, nunca el cuerpo completo:
                // podría contener datos personales o información interna.
                val mensajeServidor = try {
                    val errorJson = response.errorBody()?.string().orEmpty()
                    if (errorJson.isBlank()) {
                        "Sin mensaje de error en la respuesta"
                    } else {
                        org.json.JSONObject(errorJson)
                            .optString("message", "Sin mensaje de error identificable")
                            .take(200)
                    }
                } catch (_: Exception) {
                    "No se pudo interpretar el mensaje del backend"
                }

                android.util.Log.e(
                    "PERFIL_API",
                    "Actualización rechazada. ID solicitado: $userId; " +
                        "HTTP ${response.code()}; mensaje: $mensajeServidor"
                )
                return@withContext false
            }

            // La actualización remota ya fue confirmada. Room es una caché opcional.
            try {
                val localUser = usuarioDao.getUsuarioById(userId)
                if (localUser != null) {
                    usuarioDao.insertOrUpdateUsuario(
                        localUser.copy(
                            numero_documento = dni.trim(),
                            telefono = telefono.trim(),
                            correo = correo.trim()
                        )
                    )
                    usuarioDao.insertPerfil(
                        PerfilUsuarioEntity(
                            id_usuario = userId,
                            fecha_nacimiento = fechaNacimiento.trim(),
                            direccion = "Chiclayo Centro",
                            ciudad = "Chiclayo",
                            departamento = "Lambayeque"
                        )
                    )
                }
            } catch (localError: Exception) {
                // No informar fallo remoto si MySQL sí guardó los cambios.
                android.util.Log.w(
                    "PERFIL_API",
                    "MySQL guardó el perfil, pero Room no pudo actualizar su caché",
                    localError
                )
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("PERFIL_API", "No se pudo actualizar el perfil", e)
            false
        }
    }

    // Convierte 20/05/1992 (Humano) -> 1992-05-20 (MySQL)
    private fun fechaParaMySQL(fecha: String): String {
        val limpia = fecha.trim()
        if (limpia.contains("/")) {
            val partes = limpia.split("/")
            if (partes.size == 3) {
                val dia = partes[0].padStart(2, '0')
                val mes = partes[1].padStart(2, '0')
                val anio = partes[2]
                return "$anio-$mes-$dia"
            }
        }
        return limpia
    }

    // Convierte 1992-05-20 (MySQL) -> 20/05/1992 (Para mostrar en la App)
    private fun fechaParaApp(fecha: String?): String {
        if (fecha.isNullOrBlank()) return ""
        if (fecha.contains("-")) {
            val partes = fecha.split("-")
            if (partes.size == 3) {
                val anio = partes[0]
                val mes = partes[1]
                val dia = partes[2].take(2)
                return "$dia/$mes/$anio"
            }
        }
        return fecha
    }
}