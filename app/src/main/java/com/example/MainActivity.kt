package com.example

import android.os.Bundle
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.components.SideMenuDrawer
import com.example.components.ThemeSelectionDialog
import com.example.models.AuthProvider
import com.example.models.DriverProfile
import com.example.models.UserRole
import com.example.modules.auth.LoadingScreen
import com.example.modules.auth.LoginScreen
import com.example.modules.auth.OtpVerificationDialog
import com.example.modules.auth.RegisterScreen
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
import com.example.data.local.AppDatabase
import com.example.data.repository.SmartChiclayoRepository
import com.example.state.AppScreen
import com.example.state.SmartChiclayoViewModel
import com.example.ui.theme.SmartChiclayoTheme
import kotlinx.coroutines.launch

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
                    }
                )
            }
        } else {
            AppScreenRouter(
                viewModel = viewModel,
                onOpenMenu = {
                    coroutineScope.launch { drawerState.open() }
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
    }
}

@Composable
fun AppScreenRouter(
    viewModel: SmartChiclayoViewModel,
    onOpenMenu: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (viewModel.currentScreen) {
            AppScreen.WELCOME -> {
                WelcomeScreen(
                    onOrderTaxiClick = { viewModel.navigateTo(AppScreen.LOGIN) },
                    onLoginClick = { viewModel.navigateTo(AppScreen.LOGIN) }
                )
            }

            AppScreen.LOGIN -> {
                LoginScreen(
                    onGoogleLoginClick = {
                        viewModel.handleGoogleAuth(
                            onUserNotFound = { /* redirected to register with prefill */ },
                            onUserFound = { /* logs in */ }
                        )
                    },
                    onSendPhoneCodeClick = { phone ->
                        viewModel.requestPhoneOtp(phone)
                    },
                    onHelpClick = { viewModel.navigateTo(AppScreen.SUPPORT) },
                    onTermsClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                    onPrivacyClick = { viewModel.navigateTo(AppScreen.SETTINGS) }
                )
            }

            AppScreen.REGISTER -> {
                RegisterScreen(
                    initialNombres = viewModel.registerPrefillNombres,
                    initialApellidos = viewModel.registerPrefillApellidos,
                    initialCorreo = viewModel.registerPrefillCorreo,
                    initialTelefono = viewModel.registerPrefillTelefono,
                    authProvider = viewModel.registerAuthProvider,
                    onBackClick = { viewModel.navigateBack() },
                    onRegisterSuccess = { nombres, apellidos, dni, telefono, correo, fechaNacimiento, onResult ->
                        viewModel.registerNewUser(
                            nombres = nombres,
                            apellidos = apellidos,
                            dni = dni,
                            telefono = telefono,
                            correo = correo,
                            fechaNacimiento = fechaNacimiento,
                            onResult = onResult
                        )
                    }
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
                    onLogoutClick = { viewModel.logout() }
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
                    onLogoutClick = { viewModel.logout() },
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
