package com.example.modules.manager

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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.models.VehicleUnit
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary

@Composable
fun ManagerWizardScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }
    var isRegistered by remember { mutableStateOf(false) }

    // Step 1 State (Acreditación)
    var razonSocial by remember { mutableStateOf("ET Chiclayo Norte S.A.") }
    var ruc by remember { mutableStateOf("20601928471") }
    var cargoSolicitante by remember { mutableStateOf("Gerente de Operaciones") }
    var mtcVerified by remember { mutableStateOf(true) }

    // Step 3 State (Unidades)
    var inputPlaca by remember { mutableStateOf("") }
    var inputChofer by remember { mutableStateOf("") }
    val padronUnits = remember {
        mutableStateListOf(
            VehicleUnit("1", "M3B-120", "14", "Minibús M2/M3", "Carlos Mendoza Q."),
            VehicleUnit("2", "M4X-901", "15", "Combi Urbana", "Jorge Valdiviezo"),
            VehicleUnit("3", "T1W-445", "16", "Minibús M3", "Miguel Santisteban")
        )
    }

    // Step 4 State (Comunicación)
    var autoNotifyDrivers by remember { mutableStateOf(true) }
    var showLineActiveDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header (Back, Title, Help, Profile as in PDF Pages 9-12)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("manager_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Registro Gestor De Lín...",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Ayuda", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                }
            }

            // 4 Step Progress Bars (As explicitly requested in PDF pages 9 and 10: "Son 4 pasos, además ponle 4 barritas y pon que se está realizando el paso...")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in 1..4) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (i <= step) GoldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )
                }
            }

            // Subheader tag with step name & completion percentage
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val stepTitle = when (step) {
                    1 -> "PASO 1 DE 4 • ACREDITACIÓN"
                    2 -> "PASO 2 DE 4 • ASIGNACIÓN DE RUTAS"
                    3 -> "PASO 3 DE 4 • FLOTA Y VEHÍCULOS"
                    else -> "PASO 4 DE 4 • COMUNICACIÓN Y EQUIPO"
                }
                val stepPct = when (step) {
                    1 -> "Smart Chiclayo Pro"
                    2 -> "66% completado"
                    3 -> "75% completado"
                    else -> "100% completado"
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GoldPrimary))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stepTitle, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = GoldPrimary, letterSpacing = 0.5.sp)
                }
                Text(stepPct, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Wizard Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (step) {
                    // ==========================================
                    // PASO 1: ACREDITACIÓN (PDF Page 9)
                    // ==========================================
                    1 -> {
                        Text(
                            text = "Registro de línea",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Paso 1: Ingresa los datos oficiales de tu concesión o empresa de transporte autorizada ante la MPCH / MTC.",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )

                        // Concession Card
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CorporateFare, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Datos de la concesión oficial", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = MaterialTheme.colorScheme.onSurface)
                                            Text("Régimen regulatorio Lambayeque", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Oficial", color = Color(0xFF22C55E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Razón Social o Nombre de la concesión *Requerido", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = razonSocial,
                                    onValueChange = { razonSocial = it },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("manager_razon_social_input"),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text("RUC de la empresa (11 dígitos)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = ruc,
                                        onValueChange = { if (it.length <= 11) ruc = it },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("manager_ruc_input"),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    Button(
                                        onClick = { mtcVerified = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = GoldPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("consultar_mtc_button")
                                    ) {
                                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Consultar MTC", fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Debe estar activo y habilitado en transporte urbano e interurbano.", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Cargo del Solicitante", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = cargoSolicitante,
                                    onValueChange = { cargoSolicitante = it },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        // Legal note
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "Tus datos serán validados conforme a los registros de la Gerencia de Desarrollo Vial y Transportes de Chiclayo.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Continue Button
                        Button(
                            onClick = { step = 2 },
                            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("manager_step1_continue"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                        ) {
                            Text("Continuar →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        TextButton(
                            onClick = onBackClick,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Cancelar / Volver", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // ==========================================
                    // PASO 2: ASIGNACIÓN DE RUTAS (PDF Page 10)
                    // ==========================================
                    2 -> {
                        Text(
                            text = "Líneas a tu cargo",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Configura la línea principal de transporte bajo tu administración y añade ramales o líneas adicionales autorizadas.",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )

                        // Concession accredited banner
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("CONCESIÓN ACREDITADA", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    Text("$razonSocial • RUC $ruc", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFF22C55E).copy(alpha = 0.2f)).padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Paso 1 ✓", color = Color(0xFF22C55E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Línea Principal Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Línea 12", fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(GoldPrimary).padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("PRINCIPAL", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                    Text("• Sincronizada", color = Color(0xFF22C55E), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Text("Troncal Chiclayo Metropolitano", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Corredor Activo Bolognesi - JLO", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("18.4 km", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Recorrido Oficial Autorizado: Pimentel ↔ Av. Bolognesi ↔ Leonardo Ortiz (JLO)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Frecuencia Hora Punta", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("3 - 5 min", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    Column {
                                        Text("Cupo de Flota", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("48 unidades", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    Column {
                                        Text("Código Registro MTC", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("REG-CHIC-L12", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldPrimary)
                                    }
                                }
                            }
                        }

                        // Ramales secundarios
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Línea 08 (Borrador)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("1 en borrador", fontSize = 11.sp, color = GoldPrimary)
                                }
                                Text("Ramal Santa Victoria - Mall Aventura • 22 buses proyectados", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Buttons (Volver al paso 1 nicely formatted and continue to paso 3)
                        Button(
                            onClick = { step = 3 },
                            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("manager_step2_continue"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                        ) {
                            Text("Continuar al Paso 3 →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = { step = 1 },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("manager_back_to_step1")
                            ) {
                                Text("← Volver al Paso 1")
                            }
                            TextButton(onClick = {}) {
                                Text("Guardar borrador", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // ==========================================
                    // PASO 3: UNIDADES Y PADRÓN DE FLOTA (PDF Page 11)
                    // ==========================================
                    3 -> {
                        Text(
                            text = "Unidades y Padrón de Flota",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Registra los vehículos que conforman tu flota autorizada en Chiclayo ingresando las unidades de transporte autorizadas para esta línea.",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )

                        // Línea seleccionada chip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Línea 12 - Troncal Chiclayo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("48 cupos autorizados", fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.SemiBold)
                        }

                        // Form: Registro Rápido por Placa (Single + button as instructed in PDF page 11)
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Registro Rápido por Placa", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = inputPlaca,
                                    onValueChange = { inputPlaca = it.uppercase() },
                                    label = { Text("Placa de Rodaje (Ej. M3B-120)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("input_unit_placa"),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = inputChofer,
                                    onValueChange = { inputChofer = it },
                                    label = { Text("Nombres y Apellidos del Conductor") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("input_unit_chofer"),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Single + Agregar Unidad Button (Explicitly cleaned from PDF Page 11 note)
                                Button(
                                    onClick = {
                                        if (inputPlaca.isNotBlank() && inputChofer.isNotBlank()) {
                                            padronUnits.add(
                                                VehicleUnit(
                                                    id = "${padronUnits.size + 1}",
                                                    placa = inputPlaca.trim(),
                                                    numeroPadron = "${padronUnits.size + 14}",
                                                    categoria = "Minibús M2/M3",
                                                    conductorAsignado = inputChofer.trim()
                                                )
                                            )
                                            inputPlaca = ""
                                            inputChofer = ""
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("add_unit_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Agregar Unidad", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }

                        // List of Units
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Unidades registradas en el padrón", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${padronUnits.size} de 48 unidades", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            padronUnits.forEach { unit ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(unit.placa, fontWeight = FontWeight.Black, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Nº ${unit.id} • Padrón #${unit.numeroPadron}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text("${unit.categoria} • ${unit.conductorAsignado}", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        Box(
                                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFF22C55E).copy(alpha = 0.2f)).padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("Validado", color = Color(0xFF22C55E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Actions
                        Button(
                            onClick = { step = 4 },
                            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("manager_step3_continue"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                        ) {
                            Text("Continuar al Paso 4 →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(onClick = { step = 2 }, shape = RoundedCornerShape(10.dp)) {
                                Text("← Volver al Paso 2")
                            }
                            TextButton(onClick = {}) {
                                Text("Guardar borrador", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // ==========================================
                    // PASO 4: COMUNICACIÓN Y EQUIPO (PDF Page 12)
                    // ==========================================
                    4 -> {
                        Text(
                            text = "Grupo de Comunicación",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Canal operativo exclusivo para la Línea 12. Se han sincronizado e integrado automáticamente todos los conductores registrados en tu padrón de flota vehicular.",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )

                        // Digital PTT Walkie Talkie Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Radio, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Comunicaciones Línea 12 - Ops", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    Text("ID: CHX-L12", fontSize = 10.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("• ${padronUnits.size} Conductores vinculados automáticamente", color = Color(0xFF22C55E), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Mic, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Walkie-Talkie Digital PTT Activo (Voz instantánea con cancelación)", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Enlace directo 24/7 con Central Serenazgo y Tránsito", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        // Conductores list in Padrón
                        Text(
                            text = "Conductores del Padrón (${padronUnits.size}) • Sincronizado",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            padronUnits.forEach { unit ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(unit.conductorAsignado.take(2).uppercase(), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GoldPrimary)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(unit.conductorAsignado, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurface)
                                            Text("${unit.placa} • ${unit.categoria}", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text("Enlace activo", color = Color(0xFF22C55E), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Auto Notifications toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Notificaciones Automáticas", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Avisar por SMS y WhatsApp al registrar relevos", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = autoNotifyDrivers,
                                onCheckedChange = { autoNotifyDrivers = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = GoldPrimary)
                            )
                        }

                        // Final Action Button
                        Button(
                            onClick = {
                                isRegistered = true
                                showLineActiveDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("finalize_manager_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Finalizar Registro y Activar Línea", fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(onClick = { step = 3 }, shape = RoundedCornerShape(10.dp)) {
                                Text("← Volver al Paso 3")
                            }
                            TextButton(onClick = {}) {
                                Text("Guardar borrador", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        if (showLineActiveDialog) {
            AlertDialog(
                onDismissRequest = {
                    showLineActiveDialog = false
                    onBackClick()
                },
                title = { Text("¡Línea 12 Activada!") },
                text = {
                    Text("La concesión $razonSocial y sus unidades han sido sincronizadas en el sistema Smart Chiclayo. Los conductores ya pueden recibir asignaciones y operar en la red metropolitana.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLineActiveDialog = false
                            onBackClick()
                        }
                    ) {
                        Text("Ir al Dashboard", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
