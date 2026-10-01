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

    // Wallet balance
    var walletBalance by mutableDoubleStateOf(45.00)
    var preferredPaymentType by mutableStateOf(PaymentType.SMARTPAY)
    val transactions = mutableStateListOf(
        PaymentTransaction("TX-9901", "Viaje Taxi Smart Balta - Mall", "Hoy", "14:40", 14.50, PaymentType.SMARTPAY),
        PaymentTransaction("TX-9844", "Recarga Saldo SmartPay", "Ayer", "19:10", 50.00, PaymentType.YAPE),
        PaymentTransaction("TX-9721", "Viaje Colectivo Pimentel", "19 Oct", "09:30", 25.00, PaymentType.SMARTPAY)
    )

    // Trip history list
    val tripHistory = mutableStateListOf(
        Trip(
            id = "TRIP-001",
            codigoViaje = "CHIC-2024-8841",
            tipoServicio = TripType.TAXI_SMART,
            origen = "Hospital Regional de Lambayeque",
            origenDetalle = "Av. El Progreso",
            destino = "Plaza de Armas de Chiclayo",
            destinoDetalle = "San José",
            fecha = "Hoy",
            hora = "14:35",
            precio = 14.50,
            distanciaKm = 4.8,
            duracionMin = 18,
            metodoPago = "SmartPay",
            estado = TripStatus.COMPLETADO,
            nombreConductor = "Carlos Mendoza Q.",
            vehiculoConductor = "Toyota Yaris • Blanco",
            placaConductor = "M3B-120",
            calificacionConductor = 4.9
        ),
        Trip(
            id = "TRIP-002",
            codigoViaje = "CHIC-2024-8812",
            tipoServicio = TripType.TAXI_SMART,
            origen = "Mall Aventura Chiclayo",
            origenDetalle = "Panamericana Nte",
            destino = "Av. Santa Victoria 789",
            destinoDetalle = "Chiclayo Urbano",
            fecha = "Ayer",
            hora = "20:15",
            precio = 9.00,
            distanciaKm = 3.2,
            duracionMin = 12,
            metodoPago = "Efectivo",
            estado = TripStatus.COMPLETADO,
            nombreConductor = "Jorge Valdiviezo",
            vehiculoConductor = "Hyundai Accent • Plata",
            placaConductor = "M4X-901",
            calificacionConductor = 4.8
        ),
        Trip(
            id = "TRIP-003",
            codigoViaje = "CHIC-2024-8799",
            tipoServicio = TripType.ENVIO_EXPRESS,
            origen = "Real Plaza Chiclayo",
            origenDetalle = "Av. Bolognesi",
            destino = "Casa Blanca Suite",
            destinoDetalle = "Ampliación Saúl Cantoral",
            fecha = "22 Oct",
            hora = "11:20",
            precio = 12.00,
            distanciaKm = 5.1,
            duracionMin = 16,
            metodoPago = "Tarjeta",
            estado = TripStatus.COMPLETADO,
            nombreConductor = "Miguel Santisteban",
            vehiculoConductor = "Honda Wave • Moto",
            placaConductor = "T1W-445",
            calificacionConductor = 5.0
        ),
        Trip(
            id = "TRIP-004",
            codigoViaje = "CHIC-2024-8750",
            tipoServicio = TripType.CHICLAYO_A_CIUDADES,
            origen = "Av. Salaverry 1774",
            origenDetalle = "Chiclayo",
            destino = "Malecón de Pimentel",
            destinoDetalle = "Lambayeque Costa",
            fecha = "19 Oct",
            hora = "09:10",
            precio = 25.00,
            distanciaKm = 14.2,
            duracionMin = 28,
            metodoPago = "SmartPay",
            estado = TripStatus.COMPLETADO,
            nombreConductor = "Carlos Mendoza Q.",
            vehiculoConductor = "Toyota Avanza • Gris",
            placaConductor = "M3B-120",
            calificacionConductor = 4.9
        ),
        Trip(
            id = "TRIP-005",
            codigoViaje = "CHIC-2024-8690",
            tipoServicio = TripType.TAXI_SMART,
            origen = "Universidad Tecnológica del Perú (UTP)",
            origenDetalle = "Carretera Pimentel",
            destino = "SUITE HOTEL PIEDRA BLANCA",
            destinoDetalle = "Av. Gran Chimú",
            fecha = "15 Oct",
            hora = "18:40",
            precio = 8.50,
            distanciaKm = 2.9,
            duracionMin = 11,
            metodoPago = "Yape/Plin",
            estado = TripStatus.COMPLETADO,
            nombreConductor = "Jorge Valdiviezo",
            vehiculoConductor = "Kia Rio • Negro",
            placaConductor = "M4X-901",
            calificacionConductor = 4.7
        )
    )

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

    fun handleFacebookAuth(onUserNotFound: () -> Unit, onUserFound: () -> Unit) {
        val existing = registeredUsers.firstOrNull { it.authProvider == AuthProvider.FACEBOOK }
        if (existing != null) {
            currentUser = existing
            navigateTo(AppScreen.LOADING)
            onUserFound()
        } else {
            registerPrefillNombres = "Mayra"
            registerPrefillApellidos = "Riojas"
            registerPrefillCorreo = "mayra.fb@example.com"
            registerAuthProvider = AuthProvider.FACEBOOK
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
            calificacion = 5.0,
            totalViajes = 0,
            esVip = false,
            authProvider = registerAuthProvider
        )
        registeredUsers.add(newUser)
        currentUser = newUser
        navigateTo(AppScreen.LOADING)
        return true
    }

    fun logout() {
        currentUser = null
        currentScreen = AppScreen.WELCOME
        screenStack.clear()
        screenStack.add(AppScreen.WELCOME)
        isDrawerOpen = false
    }

    fun requestRide(destination: String, offer: Double) {
        destinationInput = destination
        userOfferPrice = String.format("%.2f", offer)
        isSearchingDriver = true
    }

    fun confirmDriverAssigned() {
        isSearchingDriver = false
        activeTrip = Trip(
            id = "TRIP-NOW",
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
