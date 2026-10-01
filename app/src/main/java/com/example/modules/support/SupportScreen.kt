package com.example.modules.support

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.GoldPrimary

data class SupportTicket(
    val ticketNumber: String,
    val fecha: String,
    val motivo: String,
    val categoria: String,
    val estado: String
)

data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
fun SupportScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }
    var reportCategory by remember { mutableStateOf("Tarifas y Cobros") }
    var reportSubject by remember { mutableStateOf("") }
    var reportDescription by remember { mutableStateOf("") }
    var showTicketSuccessDialog by remember { mutableStateOf(false) }

    val tickets = remember {
        mutableStateListOf(
            SupportTicket("TK-9041", "Hoy 12:40", "Consulta de recibo electrónico", "Facturación", "En proceso"),
            SupportTicket("TK-8812", "18 Oct", "Objeto olvidado en Taxi Yaris M3B-120", "Objetos perdidos", "Resuelto")
        )
    }

    val faqs = listOf(
        FaqItem(
            "¿Cómo solicitar un viaje en Smart Chiclayo?",
            "En la pantalla principal, verifica tu punto de partida, selecciona el tipo de servicio (Taxi Smart, Mototaxi, Colectivo o Combi), introduce tu destino o selecciona uno de los destinos populares, ajusta tu oferta de tarifa y presiona 'Solicitar viaje'."
        ),
        FaqItem(
            "¿Cómo cancelar un viaje?",
            "Si el conductor aún no ha llegado al punto de recojo, puedes presionar la 'X' en la tarjeta del viaje activo en la pantalla principal. Se te pedirá confirmar el motivo de cancelación."
        ),
        FaqItem(
            "¿Cómo cambiar el método de pago?",
            "Ingresa al menú lateral y abre 'Billetera & SmartPay'. Puedes elegir entre SmartPay Saldo, Yape, Plin, Efectivo o Tarjeta débito/crédito y marcarlo como predeterminado."
        ),
        FaqItem(
            "¿Cómo reportar incidencias en la vía pública?",
            "Desde el menú lateral, abre 'Reporte de incidencias'. El sistema detectará automáticamente tu ubicación satelital en Chiclayo. Selecciona el tipo de evento vial, la gravedad y presiona 'Enviar'."
        ),
        FaqItem(
            "¿Cómo acceder al Modo Conductor o Gestor?",
            "En el menú hamburguesa encontrarás los accesos directos 'Modo Gestor de Línea' y 'Modo Conductor' en la parte inferior."
        )
    )

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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("support_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Ayuda & Soporte 24/7",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Preguntas frecuentes y atención de solicitudes",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // FAQ Section
                item {
                    Text(
                        text = "PREGUNTAS FRECUENTES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                }

                items(faqs.indices.toList()) { index ->
                    val faq = faqs[index]
                    val isExpanded = expandedFaqIndex == index
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedFaqIndex = if (isExpanded) null else index },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = faq.question,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = GoldPrimary
                                )
                            }
                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = faq.answer,
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                // Report a problem form
                item {
                    Text(
                        text = "REPORTAR UN PROBLEMA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = reportSubject,
                                onValueChange = { reportSubject = it },
                                label = { Text("Asunto de la consulta") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("ticket_subject_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = reportDescription,
                                onValueChange = { reportDescription = it },
                                label = { Text("Describe el problema detalladamente") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth().testTag("ticket_desc_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Button(
                                onClick = {
                                    if (reportSubject.isNotBlank()) {
                                        tickets.add(
                                            0,
                                            SupportTicket(
                                                ticketNumber = "TK-${System.currentTimeMillis() % 10000}",
                                                fecha = "Hoy, ahora",
                                                motivo = reportSubject.trim(),
                                                categoria = reportCategory,
                                                estado = "Abierto"
                                            )
                                        )
                                        reportSubject = ""
                                        reportDescription = ""
                                        showTicketSuccessDialog = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_ticket_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Enviar solicitud de soporte", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // My tickets section
                item {
                    Text(
                        text = "MIS SOLICITUDES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                }

                items(tickets) { ticket ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ticket.ticketNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldPrimary)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (ticket.estado == "Resuelto") Color(0xFF22C55E).copy(alpha = 0.2f)
                                            else GoldPrimary.copy(alpha = 0.2f)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        ticket.estado,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ticket.estado == "Resuelto") Color(0xFF22C55E) else GoldPrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(ticket.motivo, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("${ticket.fecha} • Categoría: ${ticket.categoria}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (showTicketSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showTicketSuccessDialog = false },
                title = { Text("Ticket Registrado") },
                text = { Text("Tu solicitud de soporte ha sido enviada al equipo de Smart Chiclayo. Responderemos a la brevedad.") },
                confirmButton = {
                    TextButton(onClick = { showTicketSuccessDialog = false }) { Text("Aceptar") }
                }
            )
        }
    }
}
