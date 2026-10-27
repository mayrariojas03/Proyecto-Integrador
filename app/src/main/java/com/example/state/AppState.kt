package com.example.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthLoginResult
import com.example.data.repository.RegisterResult
import com.example.data.repository.SmartChiclayoRepository
import com.example.data.remote.ApiClient
import kotlinx.coroutines.launch
import com.example.models.AuthProvider
import com.example.models.DriverProfile
import com.example.models.DriverStatus
import com.example.models.IncidentReport
import com.example.models.IncidentSeverity
import com.example.models.IncidentStatus
import com.example.models.IncidentType
import com.example.models.IncomingTripRequest
import com.example.models.NotificationItem
import com.example.models.PaymentTransaction
import com.example.models.PaymentType
import com.example.models.Trip
import com.example.models.TripStatus
import com.example.models.TripType
import com.example.models.User
import com.example.models.UserRole
import com.example.ui.theme.AppThemeMode

enum class AuthTab {
    LOGIN,
    REGISTER
}

enum class AppScreen {
    WELCOME,
    LOGIN,
    REGISTER,
    LOADING,
    HOME,
    PROFILE,
    TRIP_HISTORY,
    SERVICES_CATALOG,
    INTERCITY,
    PUBLIC_TRANSIT,
    WALLET,
    NOTIFICATIONS,
    SECURITY_SOS,
    INCIDENTS,
    SETTINGS,
    SUPPORT,
    MANAGER_WIZARD,
    DRIVER_PORTAL
}

class SmartChiclayoViewModel(
    val repository: SmartChiclayoRepository? = null
) : ViewModel() {

    var currentScreen by mutableStateOf(AppScreen.WELCOME)
    val screenStack = mutableStateListOf(AppScreen.WELCOME)

    var currentTheme by mutableStateOf(AppThemeMode.OSCURO)
    var showThemeDialog by mutableStateOf(false)

    var authCurrentTab by mutableStateOf(AuthTab.LOGIN)

    val registeredUsers = mutableStateListOf<User>()
    var currentUser by mutableStateOf<User?>(null)

    // Estado de guardado para mostrar un aviso desde la interfaz.
    var guardandoPerfil by mutableStateOf(false)
        private set
    var mensajeGuardadoPerfil by mutableStateOf<String?>(null)
        private set

    fun consumirMensajeGuardadoPerfil() {
        mensajeGuardadoPerfil = null
    }

    var isDrawerOpen by mutableStateOf(false)

    var selectedServiceType by mutableStateOf(TripType.TAXI_SMART)
    var currentPickupLocation by mutableStateOf("Av. Balta 450 (Chiclayo Centro)")
    var destinationInput by mutableStateOf("")
    var userOfferPrice by mutableStateOf("7.00")
    var isSearchingDriver by mutableStateOf(false)
    var activeTrip by mutableStateOf<Trip?>(null)

    var registerPrefillNombres by mutableStateOf("")
    var registerPrefillApellidos by mutableStateOf("")
    var registerPrefillCorreo by mutableStateOf("")
    var registerPrefillTelefono by mutableStateOf("")
    var registerAuthProvider by mutableStateOf(AuthProvider.EMAIL)

    var showOtpDialog by mutableStateOf(false)
    var pendingPhoneNumber by mutableStateOf("")
    var otpCodeInput by mutableStateOf("")
    var otpErrorMsg by mutableStateOf<String?>(null)

    var walletBalance by mutableDoubleStateOf(0.00)
    var preferredPaymentType by mutableStateOf(PaymentType.SMARTPAY)
    val transactions = mutableStateListOf<PaymentTransaction>()

    val tripHistory = mutableStateListOf<Trip>()

    // Variables para el registro pendiente por verificación de correo
    var showEmailOtpDialog by mutableStateOf(false)
    var pendingRegisterEmail by mutableStateOf("")
    var pendingRegisterData by mutableStateOf<RegisterPendingData?>(null)
    val incidentsList = mutableStateListOf(
        IncidentReport(
            id = "INC-101",
            usuarioNombre = "Mayra Riojas",
            direccion = "Av. José Balta / Av. Bolognesi",
            tipo = IncidentType.TRAFICO_SEVERO,
            gravedad = IncidentSeverity.MEDIA,
            descripcion = "Congestión por desvío en intersección principal, avance lento en dirección a Mercado Modelo.",
            fecha = "Hoy",
            hora = "14:10",
            estado = IncidentStatus.ACTIVA
        )
    )

    val notificationsList = mutableStateListOf<NotificationItem>()

    var driverProfile by mutableStateOf<DriverProfile?>(null)
    var driverStatus by mutableStateOf(DriverStatus.DISPONIBLE)
    var incomingDriverRequest by mutableStateOf<IncomingTripRequest?>(
        IncomingTripRequest(
            id = "REQ-551",
            pasajeroNombre = "Mayra Riojas",
            pasajeroCalificacion = 4.96,
            origen = "Av. Balta 450 (Chiclayo Centro)",
            destino = "Mall Aventura Chiclayo",
            distanciaKm = 3.5,
            duracionMin = 12,
            tarifaOfrecida = 9.00,
            metodoPago = "Yape"
        )
    )

    var managerStep by mutableIntStateOf(1)
    var isManagerRegistered by mutableStateOf(false)

    val trustedContacts = mutableStateListOf(
        Pair("Mamá (Rosa)", "974123456"),
        Pair("Hermano (Carlos)", "979654321")
    )

    fun navigateTo(screen: AppScreen) {
        if (currentScreen != screen) {
            screenStack.add(screen)
            currentScreen = screen
        }
        isDrawerOpen = false
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            currentScreen = screenStack.last()
            return true
        }
        return false
    }

    fun handleGoogleAuth(onUserNotFound: () -> Unit, onUserFound: () -> Unit) {
        viewModelScope.launch {
            val email = "mayrariojas03@gmail.com"
            val dbUser = repository?.findUserByEmail(email)
            val memoryUser = registeredUsers.firstOrNull {
                it.authProvider == AuthProvider.GOOGLE ||
                        it.correo.equals(email, ignoreCase = true)
            }
            val existing = dbUser ?: memoryUser

            if (existing != null) {
                currentUser = existing
                walletBalance = 0.00
                tripHistory.clear()
                transactions.clear()
                navigateTo(AppScreen.LOADING)
                onUserFound()
            } else {
                registerPrefillNombres = "Mayra"
                registerPrefillApellidos = "Riojas"
                registerPrefillCorreo = email
                registerAuthProvider = AuthProvider.GOOGLE
                onUserNotFound()
                navigateTo(AppScreen.REGISTER)
            }
        }
    }

    fun requestPhoneOtp(phone: String) {
        pendingPhoneNumber = phone
        showOtpDialog = true
    }

    fun verifyPhoneOtp(
        code: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (code == "123456" || code.length >= 4) {
            showOtpDialog = false

            viewModelScope.launch {
                val dbUser = repository?.findUserByPhone(pendingPhoneNumber)

                val cleanPending = pendingPhoneNumber
                    .replace(" ", "")
                    .replace("+51", "")
                    .trim()

                val memoryUser = registeredUsers.firstOrNull {
                    it.telefono
                        .replace(" ", "")
                        .replace("+51", "") == cleanPending
                }

                val existing = dbUser ?: memoryUser

                if (existing != null) {
                    currentUser = existing
                    walletBalance = 0.00
                    tripHistory.clear()
                    transactions.clear()
                    navigateTo(AppScreen.LOADING)
                } else {
                    registerPrefillTelefono =
                        cleanPending.ifBlank { pendingPhoneNumber }

                    registerAuthProvider = AuthProvider.PHONE
                    navigateTo(AppScreen.REGISTER)
                }

                onSuccess()
            }
        } else {
            otpErrorMsg = "Código incorrecto. Prueba con 123456"
            onError("Código inválido")
        }
    }

    fun loginWithGoogleCredentials(
        email: String,
        password: String,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            if (repository != null) {
                when (
                    val result = repository.loginWithEmail(
                        email,
                        password
                    )
                ) {
                    is AuthLoginResult.Success -> {
                        currentUser = result.user
                        walletBalance = 0.00
                        tripHistory.clear()
                        transactions.clear()
                        notificationsList.clear()
                        navigateTo(AppScreen.LOADING)
                        onResult(null)
                    }

                    is AuthLoginResult.Error -> {
                        onResult(result.message)
                    }
                }
            } else {
                val found = registeredUsers.firstOrNull {
                    it.correo.equals(email.trim(), ignoreCase = true)
                }

                if (found != null) {
                    currentUser = found
                    walletBalance = 0.00
                    tripHistory.clear()
                    transactions.clear()
                    notificationsList.clear()
                    navigateTo(AppScreen.LOADING)
                    onResult(null)
                } else {
                    onResult(
                        "No se encontró una cuenta con este correo electrónico."
                    )
                }
            }
        }
    }

    fun loginWithPhoneCredentials(
        phone: String,
        password: String,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            if (repository != null) {
                when (
                    val result = repository.loginWithPhone(
                        phone,
                        password
                    )
                ) {
                    is AuthLoginResult.Success -> {
                        currentUser = result.user
                        walletBalance = 0.00
                        tripHistory.clear()
                        transactions.clear()
                        notificationsList.clear()
                        navigateTo(AppScreen.LOADING)
                        onResult(null)
                    }

                    is AuthLoginResult.Error -> {
                        onResult(result.message)
                    }
                }
            } else {
                val clean = phone
                    .replace(" ", "")
                    .replace("+51", "")
                    .trim()

                val found = registeredUsers.firstOrNull {
                    it.telefono
                        .replace(" ", "")
                        .replace("+51", "")
                        .trim() == clean
                }

                if (found != null) {
                    currentUser = found
                    walletBalance = 0.00
                    tripHistory.clear()
                    transactions.clear()
                    notificationsList.clear()
                    navigateTo(AppScreen.LOADING)
                    onResult(null)
                } else {
                    onResult(
                        "No se encontró una cuenta con este número de celular."
                    )
                }
            }
        }
    }

    fun registerNewUser(
        nombres: String,
        apellidos: String,
        dni: String,
        telefono: String,
        correo: String,
        fechaNacimiento: String,
        password: String = "",
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            if (repository != null) {
                when (
                    val result = repository.registerUser(
                        nombres = nombres,
                        apellidos = apellidos,
                        dni = dni,
                        telefono = telefono,
                        correo = correo,
                        fechaNacimiento = fechaNacimiento,
                        password = password,
                        authProvider = registerAuthProvider
                    )
                ) {
                    is RegisterResult.Success -> {
                        registeredUsers.add(result.user)
                        currentUser = result.user
                        walletBalance = 0.00
                        tripHistory.clear()
                        transactions.clear()
                        notificationsList.clear()
                        navigateTo(AppScreen.LOADING)
                        onResult(null)
                    }

                    is RegisterResult.Error -> {
                        onResult(result.message)
                    }
                }
            } else {
                val dniExists = registeredUsers.any { it.dni == dni }
                val phoneExists =
                    registeredUsers.any { it.telefono == telefono }
                val emailExists =
                    registeredUsers.any {
                        it.correo.equals(correo, ignoreCase = true)
                    }

                if (dniExists) {
                    onResult("El número de documento ya está registrado.")
                    return@launch
                }

                if (phoneExists) {
                    onResult("El teléfono ya está registrado.")
                    return@launch
                }

                if (emailExists) {
                    onResult("El correo ya está registrado.")
                    return@launch
                }

                val newUser = User(
                    id = "USR-${System.currentTimeMillis() % 10000}",
                    nombres = nombres,
                    apellidos = apellidos,
                    dni = dni,
                    telefono = telefono,
                    correo = correo,
                    fechaNacimiento = fechaNacimiento,
                    rol = UserRole.PASAJERO,
                    calificacion = 0.0,
                    totalViajes = 0,
                    esVip = false,
                    authProvider = registerAuthProvider
                )

                registeredUsers.add(newUser)
                currentUser = newUser
                walletBalance = 0.00
                tripHistory.clear()
                transactions.clear()
                notificationsList.clear()
                navigateTo(AppScreen.LOADING)
                onResult(null)
            }
        }
    }

    fun logout() {
        ApiClient.cerrarSesion()
        guardandoPerfil = false
        mensajeGuardadoPerfil = null
        currentUser = null
        walletBalance = 0.00
        tripHistory.clear()
        transactions.clear()
        notificationsList.clear()
        currentScreen = AppScreen.WELCOME
        screenStack.clear()
        screenStack.add(AppScreen.WELCOME)
        isDrawerOpen = false
        activeTrip = null
        isSearchingDriver = false
    }

    fun requestRide(destination: String, offer: Double) {
        destinationInput = destination
        userOfferPrice = String.format("%.2f", offer)
        isSearchingDriver = true
    }

    fun confirmDriverAssigned() {
        isSearchingDriver = false

        val newTrip = Trip(
            id = "TRIP-${System.currentTimeMillis() % 10000}",
            codigoViaje = "CHIC-${System.currentTimeMillis() % 10000}",
            tipoServicio = selectedServiceType,
            origen = currentPickupLocation,
            destino =
                if (destinationInput.isNotBlank())
                    destinationInput
                else
                    "Mall Aventura Chiclayo",
            fecha = "Hoy",
            hora = "Ahora",
            precio = userOfferPrice.toDoubleOrNull() ?: 7.00,
            distanciaKm = 3.6,
            duracionMin = 14,
            metodoPago = preferredPaymentType.displayName,
            estado = TripStatus.CONDUCTOR_ASIGNADO,
            nombreConductor = "Carlos Mendoza Q.",
            vehiculoConductor = "Toyota Yaris • Blanco",
            placaConductor = "M3B-120",
            calificacionConductor = 4.9
        )

        activeTrip = newTrip
        tripHistory.add(0, newTrip)

        currentUser?.let { user ->
            currentUser =
                user.copy(totalViajes = user.totalViajes + 1)

            viewModelScope.launch {
                val uId = user.id.toLongOrNull() ?: 1L
                repository?.saveTrip(newTrip, uId)
            }
        }
    }

    fun cancelActiveTrip() {
        activeTrip = null
        isSearchingDriver = false
    }

    fun submitIncident(
        tipo: IncidentType,
        gravedad: IncidentSeverity,
        direccion: String,
        descripcion: String,
        tieneFoto: Boolean,
        alertarFlota: Boolean
    ) {
        val newInc = IncidentReport(
            id = "INC-${System.currentTimeMillis() % 10000}",
            usuarioNombre =
                currentUser?.nombreCompleto ?: "Mayra Riojas",
            direccion = direccion,
            tipo = tipo,
            gravedad = gravedad,
            descripcion = descripcion,
            tieneFoto = tieneFoto,
            alertarFlotaEnRuta = alertarFlota,
            fecha = "Hoy",
            hora = "Ahora",
            estado = IncidentStatus.ACTIVA
        )

        incidentsList.add(0, newInc)
    }

    // Agrega esta función dentro de SmartChiclayoViewModel:
    fun sincronizarDatosDesdeServidor() {
        viewModelScope.launch {
            if (repository != null) {
                repository.sincronizarUsuariosDesdeMySQL()

                // Si hay un usuario conectado, refresca sus datos en pantalla
                val usuarioActual = currentUser
                if (usuarioActual != null) {
                    val usuarioActualizado = if (usuarioActual.correo.isNotBlank()) {
                        repository.findUserByEmail(usuarioActual.correo)
                    } else {
                        repository.findUserByPhone(usuarioActual.telefono)
                    }
                    if (usuarioActualizado != null) {
                        currentUser = usuarioActualizado
                    }
                }
            }
        }
    }

    fun actualizarPerfilUsuario(
        nuevoDni: String,
        nuevoTelefono: String,
        nuevoCorreo: String,
        nuevaFechaNac: String
    ) {
        if (guardandoPerfil) return
        val usuarioInicial = currentUser ?: run {
            mensajeGuardadoPerfil = "Debes iniciar sesión para editar tu perfil."
            return
        }
        val userId = usuarioInicial.id.toLongOrNull() ?: run {
            mensajeGuardadoPerfil = "No se pudo identificar tu cuenta en MySQL."
            return
        }
        val repo = repository ?: run {
            mensajeGuardadoPerfil = "El servicio de perfil no está disponible."
            return
        }

        guardandoPerfil = true
        mensajeGuardadoPerfil = null
        viewModelScope.launch {
            try {
                val ok = repo.actualizarPerfilUsuario(
                    userId = userId,
                    dni = nuevoDni,
                    telefono = nuevoTelefono,
                    correo = nuevoCorreo,
                    fechaNacimiento = nuevaFechaNac
                )
                // No aplicar el resultado si se cerró sesión o cambió la cuenta.
                if (currentUser?.id != usuarioInicial.id) return@launch
                if (ok) {
                    currentUser = currentUser?.copy(
                        dni = nuevoDni.trim(),
                        telefono = nuevoTelefono.trim(),
                        correo = nuevoCorreo.trim(),
                        fechaNacimiento = nuevaFechaNac.trim()
                    )
                    mensajeGuardadoPerfil = "Perfil guardado correctamente."
                } else {
                    mensajeGuardadoPerfil = "No se pudieron guardar los cambios. Revisa la conexión o vuelve a iniciar sesión."
                }
            } catch (e: Exception) {
                if (currentUser?.id == usuarioInicial.id) {
                    android.util.Log.e("PERFIL_API", "Error al guardar perfil", e)
                    mensajeGuardadoPerfil = "Ocurrió un error al guardar tu perfil."
                }
            } finally {
                guardandoPerfil = false
            }
        }
    }

    // Solicita el código al backend y abre el diálogo
    fun iniciarRegistroConVerificacionCorreo(
        nombres: String,
        apellidos: String,
        dni: String,
        telefono: String,
        correo: String,
        fechaNac: String,
        password: String,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Guarda temporalmente los datos
                pendingRegisterEmail = correo.trim()
                pendingRegisterData = RegisterPendingData(nombres, apellidos, dni, telefono, correo, fechaNac, password, onResult)

                // Solicita el código a Spring Boot
                ApiClient.api.enviarCodigoCorreo(mapOf("correo" to correo.trim()))

                // Muestra la ventana para ingresar el código
                showEmailOtpDialog = true
            } catch (e: Exception) {
                onResult("No se pudo enviar el correo de verificación: ${e.message}")
            }
        }
    }

    // Valida el código y si es correcto, registra en MySQL
    fun confirmarCodigoCorreoYRegistrar(
        codigo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val resp = ApiClient.api.verificarCodigoCorreo(
                    mapOf("correo" to pendingRegisterEmail, "codigo" to codigo.trim())
                )
                if (resp.isSuccessful) {
                    showEmailOtpDialog = false
                    val data = pendingRegisterData
                    if (data != null) {
                        registerNewUser(
                            nombres = data.nombres,
                            apellidos = data.apellidos,
                            dni = data.dni,
                            telefono = data.telefono,
                            correo = data.correo,
                            fechaNacimiento = data.fechaNac,
                            password = data.password,
                            onResult = data.onResult
                        )
                    }
                    onSuccess()
                } else {
                    onError("Código incorrecto. Revisa tu correo.")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            }
        }
    }

    fun iniciarSesionConCuentaGoogle(emailGoogle: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            if (repository != null) {
                // 1. Sincroniza primero con MySQL
                repository.sincronizarUsuariosDesdeMySQL()

                // 2. Busca al usuario en la base de datos por su correo de Google
                val user = repository.findUserByEmail(emailGoogle.trim())

                if (user != null) {
                    // ¡Encontrado! Inicia sesión de inmediato
                    currentUser = user
                    walletBalance = 0.00
                    tripHistory.clear()
                    navigateTo(AppScreen.LOADING)
                    onResult(null)
                } else {
                    // Si no está registrado en MySQL, le avisa para que se registre primero
                    onResult("El correo de Google ($emailGoogle) no está registrado. Regístrate primero.")
                }
            }
        }
    }

    data class RegisterPendingData(
        val nombres: String,
        val apellidos: String,
        val dni: String,
        val telefono: String,
        val correo: String,
        val fechaNac: String,
        val password: String,
        val onResult: (String?) -> Unit
    )
}