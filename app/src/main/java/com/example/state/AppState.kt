package com.example.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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

class SmartChiclayoViewModel : ViewModel() {

    // Current navigation state
    var currentScreen by mutableStateOf(AppScreen.WELCOME)
    val screenStack = mutableStateListOf(AppScreen.WELCOME)

    // Theme state (Claro, Oscuro, Rosa pastel, Verde pastel, Azul pastel)
    var currentTheme by mutableStateOf(AppThemeMode.OSCURO)
    var showThemeDialog by mutableStateOf(false)

    // User accounts state - As requested: NO ACCOUNTS PRE-REGISTERED INITIALLY
    val registeredUsers = mutableStateListOf<User>()
    var currentUser by mutableStateOf<User?>(null)

    // Drawer state
    var isDrawerOpen by mutableStateOf(false)

    // Selected trip service on Home screen
    var selectedServiceType by mutableStateOf(TripType.TAXI_SMART)
    var currentPickupLocation by mutableStateOf("Av. Balta 450 (Chiclayo Centro)")
    var destinationInput by mutableStateOf("")
    var userOfferPrice by mutableStateOf("7.00")
    var isSearchingDriver by mutableStateOf(false)
    var activeTrip by mutableStateOf<Trip?>(null)

    // Pre-fill fields for registration when redirected from social/phone auth
    var registerPrefillNombres by mutableStateOf("")
    var registerPrefillApellidos by mutableStateOf("")
    var registerPrefillCorreo by mutableStateOf("")
    var registerPrefillTelefono by mutableStateOf("")
    var registerAuthProvider by mutableStateOf(AuthProvider.EMAIL)

    // OTP verification modal state
    var showOtpDialog by mutableStateOf(false)
    var pendingPhoneNumber by mutableStateOf("")
    var otpCodeInput by mutableStateOf("")
    var otpErrorMsg by mutableStateOf<String?>(null)

    // Wallet balance - Initialized to 0.00 for new users
    var walletBalance by mutableDoubleStateOf(0.00)
    var preferredPaymentType by mutableStateOf(PaymentType.SMARTPAY)
    val transactions = mutableStateListOf<PaymentTransaction>()

    // Trip history list - Initialized empty for new users
    val tripHistory = mutableStateListOf<Trip>()

    // Incidents state
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

    // Notifications
    val notificationsList = mutableStateListOf(
        NotificationItem(
            id = "NOTIF-1",
            categoria = NotificationCategory.VIAJES,
            titulo = "Viaje completado con éxito",
            mensaje = "Tu viaje hacia Plaza de Armas culminó por S/ 14.50. ¡Gracias por viajar con Smart Chiclayo!",
            fecha = "Hoy",
            hora = "14:55",
            leida = false
        ),
        NotificationItem(
            id = "NOTIF-2",
            categoria = NotificationCategory.SEGURIDAD,
            titulo = "Monitoreo SOS Activo",
            mensaje = "Tus contactos de confianza pueden recibir tu ruta en tiempo real ante cualquier emergencia.",
            fecha = "Ayer",
            hora = "09:30",
            leida = false
        )
    )

    // Driver mode state
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

    // Manager wizard step (1 to 4)
    var managerStep by mutableIntStateOf(1)
    var isManagerRegistered by mutableStateOf(false)

    // Trusted contacts
    val trustedContacts = mutableStateListOf(
        Pair("Mamá (Rosa)", "974123456"),
        Pair("Hermano (Carlos)", "979654321")
    )

    // Navigation methods
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

    // Auth actions
    fun handleGoogleAuth(onUserNotFound: () -> Unit, onUserFound: () -> Unit) {
        // Look for Google registered user in registeredUsers
        val existing = registeredUsers.firstOrNull { it.authProvider == AuthProvider.GOOGLE || it.correo == "mayrariojas03@gmail.com" }
        if (existing != null) {
            currentUser = existing
            navigateTo(AppScreen.LOADING)
            onUserFound()
        } else {
            // No user registered yet, prefill and go to register
            registerPrefillNombres = "Mayra"
            registerPrefillApellidos = "Riojas"
            registerPrefillCorreo = "mayrariojas03@gmail.com"
            registerAuthProvider = AuthProvider.GOOGLE
            onUserNotFound()
            navigateTo(AppScreen.REGISTER)
        }
    }


    fun requestPhoneOtp(phone: String) {
        pendingPhoneNumber = phone
        showOtpDialog = true
    }

    fun verifyPhoneOtp(code: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (code == "123456" || code.length >= 4) {
            showOtpDialog = false
            val existing = registeredUsers.firstOrNull { it.telefono == pendingPhoneNumber }
            if (existing != null) {
                currentUser = existing
                navigateTo(AppScreen.LOADING)
            } else {
                registerPrefillTelefono = pendingPhoneNumber
                registerAuthProvider = AuthProvider.PHONE
                navigateTo(AppScreen.REGISTER)
            }
            onSuccess()
        } else {
            otpErrorMsg = "Código incorrecto. Prueba con 123456"
            onError("Código inválido")
        }
    }

    fun registerNewUser(
        nombres: String,
        apellidos: String,
        dni: String,
        telefono: String,
        correo: String,
        fechaNacimiento: String
    ): Boolean {
        // Validation check
        val dniExists = registeredUsers.any { it.dni == dni }
        val phoneExists = registeredUsers.any { it.telefono == telefono }
        val emailExists = registeredUsers.any { it.correo.equals(correo, ignoreCase = true) }

        if (dniExists || phoneExists || emailExists) {
            return false
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
        navigateTo(AppScreen.LOADING)
        return true
    }

    fun logout() {
        currentUser = null
        walletBalance = 0.00
        tripHistory.clear()
        transactions.clear()
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
            destino = if (destinationInput.isNotBlank()) destinationInput else "Mall Aventura Chiclayo",
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
        // Add to trip history so user can see it
        tripHistory.add(0, newTrip)
        currentUser?.let { user ->
            currentUser = user.copy(totalViajes = user.totalViajes + 1)
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
            usuarioNombre = currentUser?.nombreCompleto ?: "Mayra Riojas",
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
