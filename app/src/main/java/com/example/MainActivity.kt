@file:JvmName("MainActivityKt")

package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier

import com.example.components.SideMenuDrawer
import com.example.components.ThemeSelectionDialog

import com.example.data.local.AppDatabase
import com.example.data.remote.GoogleAuthRequest
import com.example.data.repository.SmartChiclayoRepository

import com.example.models.DriverProfile
import com.example.models.User
import com.example.models.UserRole
import com.example.models.AuthProvider

import com.example.modules.auth.AuthScreen
import com.example.modules.auth.LoadingScreen
import com.example.modules.auth.OtpVerificationDialog
import com.example.modules.auth.WelcomeScreen

import com.example.modules.driver.DriverPortalScreen
import com.example.modules.incidents.IncidentReportScreen
import com.example.modules.manager.ManagerWizardScreen
import com.example.modules.notifications.NotificationsScreen

import com.example.modules.passenger.HomeScreen
import com.example.modules.passenger.IntercityScreen
import com.example.modules.passenger.PublicTransitScreen
import com.example.modules.passenger.ServicesScreen
import com.example.modules.passenger.TripHistoryScreen

import com.example.modules.payments.WalletScreen
import com.example.modules.profile.ProfileScreen
import com.example.modules.security.SecurityScreen
import com.example.modules.settings.SettingsScreen
import com.example.modules.support.SupportScreen

import com.example.state.AppScreen
import com.example.state.AuthTab
import com.example.state.SmartChiclayoViewModel

import com.example.ui.theme.SmartChiclayoTheme

import kotlinx.coroutines.launch

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import com.example.data.remote.ApiClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getInstance(applicationContext)
        val repository = SmartChiclayoRepository(database.usuarioDao(), database.viajeDao())
        val appViewModel = SmartChiclayoViewModel(repository)
        setContent {
            SmartChiclayoApp(viewModel = appViewModel)
        }
    }
}

@Composable
fun SmartChiclayoApp(viewModel: SmartChiclayoViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Solicita un ID token cuya audiencia es el cliente OAuth Web.
    val gso = remember(context) {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(R.string.google_web_client_id))
            .build()
    }
    val googleSignInClient = remember(context, gso) {
        GoogleSignIn.getClient(context, gso)
    }

    val cerrarSesion: () -> Unit = {
        ApiClient.cerrarSesion()
        viewModel.logout()
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                .getResult(com.google.android.gms.common.api.ApiException::class.java)
            val idToken = account.idToken

            if (idToken.isNullOrBlank()) {
                Log.e("GOOGLE_AUTH", "Google no entregó un ID token")
                Toast.makeText(context, "No se obtuvo el token de Google", Toast.LENGTH_LONG).show()
            } else {
                coroutineScope.launch {
                    try {
                        val response = ApiClient.api.autenticarConGoogle(
                            GoogleAuthRequest(idToken = idToken)
                        )
                        val body = response.body()
                        if (response.isSuccessful && body?.success == true) {
                            val token = body.accessToken
                            val usuario = body.usuario
                            if (token.isNullOrBlank() || usuario == null ||
                                !body.tokenType.equals("Bearer", ignoreCase = true) ||
                                body.expiresIn <= 0L
                            ) {
                                Log.e("GOOGLE_AUTH", "El backend no devolvio una sesion completa")
                                Toast.makeText(
                                    context,
                                    "No se pudo iniciar la sesion. Intenta nuevamente",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                ApiClient.establecerToken(token)
                                viewModel.currentUser = User(
                                    id = usuario.idUsuario.toString(),
                                    nombres = usuario.nombres,
                                    apellidos = usuario.apellidos,
                                    dni = "",
                                    telefono = "",
                                    correo = usuario.correo,
                                    rol = when (usuario.tipoUsuario.uppercase()) {
                                        "CONDUCTOR" -> UserRole.CONDUCTOR
                                        "GESTOR_LINEA" -> UserRole.GESTOR_LINEA
                                        else -> UserRole.PASAJERO
                                    },
                                    authProvider = AuthProvider.GOOGLE
                                )
                                viewModel.navigateTo(AppScreen.LOADING)
                                Toast.makeText(
                                    context,
                                    "Sesion iniciada con Google",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            val mensaje = when (response.code()) {
                                409 -> "El correo ya pertenece a una cuenta existente"
                                401 -> "Google no pudo validar la cuenta"
                                503 -> "No se pudo verificar Google. Intenta de nuevo"
                                else -> "Error del backend (${response.code()})"
                            }
                            Log.e("GOOGLE_AUTH", mensaje)
                            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Log.e("GOOGLE_AUTH", "Error al comunicarse con Spring Boot", e)
                        Toast.makeText(
                            context,
                            "No se pudo conectar con el backend",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } catch (e: com.google.android.gms.common.api.ApiException) {
            Log.e("GOOGLE_AUTH", "Google Sign-In falló: ${e.statusCode}", e)
            Toast.makeText(
                context,
                "Error de Google Sign-In: ${e.statusCode}",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Log.e("GOOGLE_AUTH", "Error inesperado de Google Sign-In", e)
            Toast.makeText(
                context,
                "Error inesperado durante el inicio de sesión",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        try {
            // Sincroniza con MySQL al iniciar o recargar la app
            viewModel.sincronizarDatosDesdeServidor()
            Log.d("SMART_API", "Sincronización con MySQL completada.")
        } catch (e: Exception) {
            Log.e("SMART_API", "Error al sincronizar con el backend: ${e.message}")
        }
    }

    // Mostrar una sola vez el resultado de guardar el perfil.
    LaunchedEffect(viewModel.mensajeGuardadoPerfil) {
        val mensaje = viewModel.mensajeGuardadoPerfil
        if (!mensaje.isNullOrBlank()) {
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
            viewModel.consumirMensajeGuardadoPerfil()
        }
    }

    // Sync drawer open state with viewModel
    LaunchedEffect(viewModel.isDrawerOpen) {
        if (viewModel.isDrawerOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    LaunchedEffect(drawerState.isOpen) {
        viewModel.isDrawerOpen = drawerState.isOpen
    }

    // Handle Android system back gesture
    BackHandler(enabled = viewModel.currentScreen != AppScreen.WELCOME) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (viewModel.currentScreen != AppScreen.HOME) {
            val handled = viewModel.navigateBack()
            if (!handled) {
                viewModel.navigateTo(AppScreen.HOME)
            }
        }
    }

    SmartChiclayoTheme(themeMode = viewModel.currentTheme) {
        // Full Drawer Wrap for screens that support menu access
        val showDrawer = viewModel.currentScreen !in listOf(
            AppScreen.WELCOME,
            AppScreen.LOGIN,
            AppScreen.REGISTER,
            AppScreen.LOADING
        )

        if (showDrawer) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        SideMenuDrawer(
                            currentUser = viewModel.currentUser,
                            currentScreen = viewModel.currentScreen,
                            unreadNotifCount = viewModel.notificationsList.count { !it.leida },
                            onNavigate = { targetScreen ->
                                coroutineScope.launch { drawerState.close() }
                                viewModel.navigateTo(targetScreen)
                            }
                        )
                    }
                }
            ) {
                AppScreenRouter(
                    viewModel = viewModel,
                    onOpenMenu = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onLogout = cerrarSesion,
                    onGoogleLoginClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleLauncher.launch(googleSignInClient.signInIntent)
                        }
                    }
                )
            }
        } else {
            AppScreenRouter(
                viewModel = viewModel,
                onOpenMenu = {
                    coroutineScope.launch { drawerState.open() }
                },
                onLogout = cerrarSesion,
                onGoogleLoginClick = {
                    googleSignInClient.signOut().addOnCompleteListener {
                        googleLauncher.launch(googleSignInClient.signInIntent)
                    }
                }
            )
        }

        // Theme Selection Dialog
        if (viewModel.showThemeDialog) {
            ThemeSelectionDialog(
                currentTheme = viewModel.currentTheme,
                onSelectTheme = { viewModel.currentTheme = it },
                onDismiss = { viewModel.showThemeDialog = false }
            )
        }

        // SMS OTP Verification Modal
        if (viewModel.showOtpDialog) {
            OtpVerificationDialog(
                phoneNumber = viewModel.pendingPhoneNumber,
                onVerifyCode = { code ->
                    viewModel.verifyPhoneOtp(
                        code = code,
                        onSuccess = {},
                        onError = {}
                    )
                },
                onDismiss = { viewModel.showOtpDialog = false }
            )
        }

        // Diálogo emergente para el código de verificación por correo
        if (viewModel.showEmailOtpDialog) {
            com.example.modules.auth.OtpVerificationDialog(
                phoneNumber = "Correo: " + viewModel.pendingRegisterEmail,
                onVerifyCode = { codigo ->
                    viewModel.confirmarCodigoCorreoYRegistrar(
                        codigo = codigo,
                        onSuccess = {
                            Toast.makeText(context, "¡Correo verificado con éxito!", Toast.LENGTH_SHORT).show()
                        },
                        onError = { err ->
                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onDismiss = { viewModel.showEmailOtpDialog = false }
            )
        }
    }
}

@Composable
fun AppScreenRouter(
    viewModel: SmartChiclayoViewModel,
    onOpenMenu: () -> Unit,
    onLogout: () -> Unit,
    onGoogleLoginClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (viewModel.currentScreen) {
            AppScreen.WELCOME -> {
                WelcomeScreen(
                    onOrderTaxiClick = {
                        viewModel.authCurrentTab = AuthTab.REGISTER
                        viewModel.navigateTo(AppScreen.LOGIN)
                    },
                    onLoginClick = {
                        viewModel.authCurrentTab = AuthTab.LOGIN
                        viewModel.navigateTo(AppScreen.LOGIN)
                    }
                )
            }

            AppScreen.LOGIN, AppScreen.REGISTER -> {
                AuthScreen(
                    currentTab = viewModel.authCurrentTab,
                    onTabChange = { newTab -> viewModel.authCurrentTab = newTab },
                    onGoogleLoginClick = onGoogleLoginClick,
                    onLoginWithGoogle = { email, password, onResult ->
                        viewModel.loginWithGoogleCredentials(email, password, onResult)
                    },
                    onLoginWithPhone = { phone, password, onResult ->
                        viewModel.loginWithPhoneCredentials(phone, password, onResult)
                    },
                    onRequestPhoneOtp = { phone ->
                        viewModel.requestPhoneOtp(phone)
                    },
                    onRegisterSubmit = { nombres, apellidos, dni, telefono, correo, fechaNac, password, onResult ->
                        viewModel.registerNewUser(
                            nombres = nombres,
                            apellidos = apellidos,
                            dni = dni,
                            telefono = telefono,
                            correo = correo,
                            fechaNacimiento = fechaNac,
                            password = password,
                            onResult = onResult
                        )
                    },
                    onBackClick = {
                        val handled = viewModel.navigateBack()
                        if (!handled) {
                            viewModel.navigateTo(AppScreen.WELCOME)
                        }
                    },
                    onHelpClick = { viewModel.navigateTo(AppScreen.SUPPORT) },
                    onTermsClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                    onPrivacyClick = { viewModel.navigateTo(AppScreen.SETTINGS) }
                )
            }

            AppScreen.LOADING -> {
                LoadingScreen(
                    onLoadingComplete = {
                        viewModel.navigateTo(AppScreen.HOME)
                    }
                )
            }

            AppScreen.HOME -> {
                HomeScreen(
                    selectedService = viewModel.selectedServiceType,
                    pickupLocation = viewModel.currentPickupLocation,
                    destinationInput = viewModel.destinationInput,
                    activeTrip = viewModel.activeTrip,
                    isSearching = viewModel.isSearchingDriver,
                    isDarkTheme = viewModel.currentTheme.isDark,
                    onMenuClick = onOpenMenu,
                    onThemeToggleClick = { viewModel.showThemeDialog = true },
                    onSelectService = { viewModel.selectedServiceType = it },
                    onDestinationSelect = { dest, price ->
                        viewModel.destinationInput = dest
                        viewModel.userOfferPrice = String.format("%.2f", price)
                    },
                    onRequestRideClick = {
                        val offer = viewModel.userOfferPrice.toDoubleOrNull() ?: 7.0
                        viewModel.requestRide(
                            destination = if (viewModel.destinationInput.isNotBlank()) viewModel.destinationInput else "Mall Aventura Chiclayo",
                            offer = offer
                        )
                    },
                    onCancelTrip = { viewModel.cancelActiveTrip() },
                    onSimulateDriverArrived = { viewModel.confirmDriverAssigned() },
                    onViewFavoritesClick = { viewModel.navigateTo(AppScreen.TRIP_HISTORY) }
                )
            }

            AppScreen.PROFILE -> {
                ProfileScreen(
                    currentUser = viewModel.currentUser,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onLogoutClick = onLogout,
                    onSaveProfile = { dni, phone, email, fechaNac -> viewModel.actualizarPerfilUsuario(nuevoDni = dni,
                        nuevoTelefono = phone,
                        nuevoCorreo = email,
                        nuevaFechaNac = fechaNac)
                    }
                )
            }

            AppScreen.TRIP_HISTORY -> {
                TripHistoryScreen(
                    trips = viewModel.tripHistory,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onHelpWithTrip = { viewModel.navigateTo(AppScreen.SUPPORT) }
                )
            }

            AppScreen.SERVICES_CATALOG -> {
                ServicesScreen(
                    currentService = viewModel.selectedServiceType,
                    onSelectService = {
                        viewModel.selectedServiceType = it
                        viewModel.navigateTo(AppScreen.HOME)
                    },
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }

            AppScreen.INTERCITY -> {
                IntercityScreen(
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onBookIntercity = { _, _ -> }
                )
            }

            AppScreen.PUBLIC_TRANSIT -> {
                PublicTransitScreen(
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }

            AppScreen.WALLET -> {
                WalletScreen(
                    balance = viewModel.walletBalance,
                    preferredMethod = viewModel.preferredPaymentType,
                    transactions = viewModel.transactions,
                    onSelectPreferredMethod = { viewModel.preferredPaymentType = it },
                    onTopUpBalance = { amount -> viewModel.walletBalance += amount },
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }

            AppScreen.NOTIFICATIONS -> {
                NotificationsScreen(
                    notifications = viewModel.notificationsList,
                    onMarkAllAsRead = {
                        val updated = viewModel.notificationsList.map { it.copy(leida = true) }
                        viewModel.notificationsList.clear()
                        viewModel.notificationsList.addAll(updated)
                    },
                    onDeleteNotification = { id ->
                        viewModel.notificationsList.removeAll { it.id == id }
                    },
                    onNotificationClick = { notif ->
                        val index = viewModel.notificationsList.indexOfFirst { it.id == notif.id }
                        if (index >= 0) {
                            viewModel.notificationsList[index] = notif.copy(leida = true)
                        }
                    },
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }

            AppScreen.SECURITY_SOS -> {
                SecurityScreen(
                    activeTrip = viewModel.activeTrip,
                    trustedContacts = viewModel.trustedContacts,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }

            AppScreen.INCIDENTS -> {
                IncidentReportScreen(
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onSubmitIncident = { tipo, gravedad, direccion, descripcion, tieneFoto, alertarFlota ->
                        viewModel.submitIncident(
                            tipo = tipo,
                            gravedad = gravedad,
                            direccion = direccion,
                            descripcion = descripcion,
                            tieneFoto = tieneFoto,
                            alertarFlota = alertarFlota
                        )
                    }
                )
            }

            AppScreen.SETTINGS -> {
                SettingsScreen(
                    currentUser = viewModel.currentUser,
                    currentTheme = viewModel.currentTheme,
                    onThemeSelect = { viewModel.currentTheme = it },
                    onLogoutClick = onLogout,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }

            AppScreen.SUPPORT -> {
                SupportScreen(
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }

            AppScreen.MANAGER_WIZARD -> {
                ManagerWizardScreen(
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }

            AppScreen.DRIVER_PORTAL -> {
                DriverPortalScreen(
                    driverProfile = viewModel.driverProfile,
                    incomingRequest = viewModel.incomingDriverRequest,
                    onRegisterDriver = { nombres, dni, licencia, placa ->
                        viewModel.driverProfile = DriverProfile(
                            id = "DRV-1",
                            nombres = nombres,
                            apellidos = "",
                            dni = dni,
                            telefono = "974123456",
                            correo = "conductor@smartchiclayo.pe",
                            numeroLicencia = licencia,
                            categoriaLicencia = "A-IIa",
                            fechaVencimiento = "12/2028",
                            placa = placa
                        )
                    },
                    onBackToPassenger = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
        }
    }
}
