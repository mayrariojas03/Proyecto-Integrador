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
import kotlinx.coroutines.launch
import com.example.models.AuthProvider
import com.example.models.DriverProfile
import com.example.models.DriverStatus
import com.example.models.IncidentReport
import com.example.models.IncidentSeverity
import com.example.models.IncidentStatus
import com.example.models.IncidentType
import com.example.models.IncomingTripRequest
import com.example.models.NotificationCategory
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
}