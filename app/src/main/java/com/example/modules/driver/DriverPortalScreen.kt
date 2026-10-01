package com.example.modules.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.DriverProfile
import com.example.models.DriverStatus
import com.example.models.IncomingTripRequest
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary

@Composable
fun DriverPortalScreen(
    driverProfile: DriverProfile?,
    incomingRequest: IncomingTripRequest?,
    onRegisterDriver: (String, String, String, String) -> Unit,
    onBackToPassenger: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDriverRegistered by remember { mutableStateOf(driverProfile != null) }
    var currentDriverStatus by remember { mutableStateOf(DriverStatus.DISPONIBLE) }
    var showRegisterModal by remember { mutableStateOf(false) }

    // Trip execution states: IDLE -> ACCEPTED -> ARRIVED -> IN_PROGRESS -> COMPLETED
    var tripState by remember { mutableStateOf("IDLE") } // IDLE, ACCEPTED, ARRIVED, IN_PROGRESS, COMPLETED
    var currentActiveRequest by remember { mutableStateOf(incomingRequest) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0D0E11)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackToPassenger,
                        modifier = Modifier.testTag("driver_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDriverRegistered) "Panel de Conductor" else "Gana con nosotros",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF261E0A))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GoldPrimary))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chiclayo", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (!isDriverRegistered) {
                // ==========================================
                // SCREEN: GANA CON NOSOTROS (PDF Page 8)
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Gana con nosotros",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Únete a la mayor red de conductores y repartidores de Chiclayo y Lambayeque.",
                            fontSize = 13.sp,
                            color = Color(0xFFA0A3B1)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Benefits List (Matching PDF Page 8)
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            BenefitRow(
                                icon = Icons.Default.AccessTime,
                                title = "Horarios flexibles",
                                subtitle = "Maneja cuando tú decidas sin jefes ni turnos obligatorios."
                            )
                            BenefitRow(
                                icon = Icons.Default.PriceChange,
                                title = "Tus propios precios",
                                subtitle = "Negocia y acepta tarifas justas en tiempo real con los pasajeros."
                            )
                            BenefitRow(
                                icon = Icons.Default.Percent,
                                title = "Bajos pagos por servicio",
                                subtitle = "0% comisión de lanzamiento en toda la región Chiclayo."
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        // Role Selection Cards (With history-style icons as requested in PDF page 8: "quiero que los iconos sean de tipo historial")
                        Text(
                            text = "SELECCIONA TU MODALIDAD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B7280),
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Conductor Card (history-style vehicle icon)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showRegisterModal = true }
                                .testTag("select_driver_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2F3C))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF261E0A))
                                            .border(1.dp, GoldPrimary, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WorkHistory,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text("Conductor", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                        Text("Maneja tu auto y genera ingresos diarios", fontSize = 12.sp, color = Color(0xFFA0A3B1))
                                    }
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Repartidor Card (history-style courier icon)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showRegisterModal = true }
                                .testTag("select_courier_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2F3C))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1E2419))
                                            .border(1.dp, Color(0xFF22C55E), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = Color(0xFF22C55E),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text("Repartidor", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                        Text("Entrega encomiendas en moto o bici", fontSize = 12.sp, color = Color(0xFFA0A3B1))
                                    }
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color(0xFF22C55E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Bottom links (Matching PDF Page 8)
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.clickable { isDriverRegistered = true }.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ya tengo una cuenta de conductor", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.clickable { onBackToPassenger() }.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color(0xFFA0A3B1), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ir al modo pasajero", color = Color(0xFFA0A3B1), fontSize = 13.sp)
                        }
                    }
                }
            } else {
                // ==========================================
                // SCREEN: ACTIVE DRIVER CONSOLE
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Status Toggle Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2F3C))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ESTADO ACTUAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldPrimary, letterSpacing = 0.5.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (currentDriverStatus == DriverStatus.DISPONIBLE) Color(0xFF22C55E)
                                                else Color(0xFFEF4444)
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = currentDriverStatus.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    currentDriverStatus = if (currentDriverStatus == DriverStatus.DISPONIBLE) DriverStatus.DESCONECTADO else DriverStatus.DISPONIBLE
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentDriverStatus == DriverStatus.DISPONIBLE) Color(0xFF22C55E) else GoldPrimary,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.testTag("toggle_driver_online_button")
                            ) {
                                Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (currentDriverStatus == DriverStatus.DISPONIBLE) "En línea" else "Conectarme", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Incoming Ride Request or Active Ride
                    if (currentActiveRequest != null && tripState == "IDLE") {
                        val req = currentActiveRequest!!
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2430)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0xFF261E0A)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("MR", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 14.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(req.pasajeroNombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                            Text("★ ${req.pasajeroCalificacion} Pasajero VIP", fontSize = 11.5.sp, color = GoldPrimary)
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("S/ ${String.format("%.2f", req.tarifaOfrecida)}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = GoldPrimary)
                                        Text("Pago: ${req.metodoPago}", fontSize = 11.sp, color = Color(0xFFA0A3B1))
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Punto de recojo: ${req.origen}", fontSize = 12.5.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text("Destino: ${req.destino}", fontSize = 12.5.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text("Distancia: ${req.distanciaKm} km • Tiempo: ~${req.duracionMin} min", fontSize = 11.sp, color = Color(0xFFA0A3B1))

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { currentActiveRequest = null },
                                        modifier = Modifier.weight(1f).height(46.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Rechazar", color = Color(0xFFEF4444))
                                    }

                                    Button(
                                        onClick = { tripState = "ACCEPTED" },
                                        modifier = Modifier.weight(1f).height(46.dp).testTag("accept_trip_button"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                                    ) {
                                        Text("Aceptar viaje", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else if (tripState != "IDLE") {
                        // Driver trip lifecycle (As required: Aceptar -> Ir al punto de recojo -> "He llegado" -> Iniciar viaje -> Viaje en curso -> Finalizar viaje -> Resumen -> Disponible)
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val statusLabel = when (tripState) {
                                    "ACCEPTED" -> "En camino al punto de recojo"
                                    "ARRIVED" -> "En el punto de recojo (Esperando al pasajero)"
                                    "IN_PROGRESS" -> "Viaje en curso hacia destino"
                                    else -> "Viaje culminado"
                                }

                                Text("ESTADO DEL VIAJE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                Text(statusLabel, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Pasajero: Mayra Riojas • Tarifa pactada: S/ 9.00", fontSize = 12.5.sp, color = Color(0xFFA0A3B1))

                                Spacer(modifier = Modifier.height(14.dp))

                                when (tripState) {
                                    "ACCEPTED" -> {
                                        Button(
                                            onClick = { tripState = "ARRIVED" },
                                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("driver_arrived_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("He llegado al punto de recojo", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    "ARRIVED" -> {
                                        Button(
                                            onClick = { tripState = "IN_PROGRESS" },
                                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("driver_start_trip_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.Black),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Iniciar viaje", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    "IN_PROGRESS" -> {
                                        Button(
                                            onClick = { tripState = "COMPLETED" },
                                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("driver_finish_trip_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Finalizar viaje y cobrar S/ 9.00", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    "COMPLETED" -> {
                                        Text("¡Viaje finalizado con éxito! El cobro de S/ 9.00 fue acreditado.", color = Color(0xFF22C55E), fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                tripState = "IDLE"
                                                currentActiveRequest = null
                                            },
                                            modifier = Modifier.fillMaxWidth().height(48.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Volver a estado disponible", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Earnings summary
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2F3C))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Ganancias de hoy", fontSize = 11.sp, color = Color(0xFFA0A3B1))
                                Text("S/ 114.50", fontWeight = FontWeight.Black, fontSize = 20.sp, color = GoldPrimary)
                            }
                            Column {
                                Text("Viajes hoy", fontSize = 11.sp, color = Color(0xFFA0A3B1))
                                Text("8 completados", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                            Column {
                                Text("Calificación", fontSize = 11.sp, color = Color(0xFFA0A3B1))
                                Text("4.92 ★", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GoldPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onBackToPassenger,
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("return_to_passenger_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Regresar al modo pasajero", color = Color.White)
                    }
                }
            }
        }

        // Driver Registration Modal
        if (showRegisterModal) {
            var inputNombres by remember { mutableStateOf("") }
            var inputDni by remember { mutableStateOf("") }
            var inputLicencia by remember { mutableStateOf("") }
            var inputPlaca by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showRegisterModal = false },
                title = { Text("Registro de Conductor Chiclayo", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = inputNombres,
                            onValueChange = { inputNombres = it },
                            label = { Text("Nombres y Apellidos") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputDni,
                            onValueChange = { if (it.length <= 8) inputDni = it },
                            label = { Text("DNI (8 dígitos)") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputLicencia,
                            onValueChange = { inputLicencia = it },
                            label = { Text("Número de Brevete / Licencia MTC") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputPlaca,
                            onValueChange = { inputPlaca = it.uppercase() },
                            label = { Text("Placa del vehículo (Ej. M3B-120)") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (inputNombres.isNotBlank() && inputLicencia.isNotBlank()) {
                                onRegisterDriver(inputNombres, inputDni, inputLicencia, inputPlaca)
                                isDriverRegistered = true
                                showRegisterModal = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("Completar registro", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRegisterModal = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun BenefitRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E212B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = Color.White)
            Text(subtitle, fontSize = 12.sp, color = Color(0xFFA0A3B1), lineHeight = 16.sp)
        }
    }
}
