package com.example.modules.passenger

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.TripType
import com.example.ui.theme.GoldPrimary

data class ServiceDetail(
    val type: TripType,
    val icon: ImageVector,
    val capacity: String,
    val description: String,
    val basePrice: String,
    val eta: String,
    val badge: String,
    val badgeColor: Color
)

@Composable
fun ServicesScreen(
    currentService: TripType,
    onSelectService: (TripType) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val services = listOf(
        ServiceDetail(
            type = TripType.TAXI_SMART,
            icon = Icons.Default.LocalTaxi,
            capacity = "Hasta 4 pasajeros",
            description = "Servicio de taxi ejecutivo y estándar directo puerta a puerta en todo Chiclayo, con aire acondicionado y tarifa negociable.",
            basePrice = "Tarifa base desde S/ 5.00",
            eta = "Llegada en 3 - 5 min",
            badge = "RÁPIDO Y CÓMODO",
            badgeColor = GoldPrimary
        ),
        ServiceDetail(
            type = TripType.MOTOTAXI,
            icon = Icons.Default.TwoWheeler,
            capacity = "Hasta 3 pasajeros",
            description = "Desplazamiento ágil y económico para distancias cortas y zonas residenciales de Chiclayo y distritos.",
            basePrice = "Tarifa base desde S/ 3.00",
            eta = "Llegada en 2 - 4 min",
            badge = "MÁS ECONÓMICO",
            badgeColor = Color(0xFF22C55E)
        ),
        ServiceDetail(
            type = TripType.COLECTIVO,
            icon = Icons.Default.Group,
            capacity = "Hasta 4 pasajeros en ruta fija",
            description = "Transporte compartido en rutas rápidas entre Chiclayo Centro, Lambayeque, Pimentel y Ferreñafe con paraderos autorizados.",
            basePrice = "Pasaje fijo desde S/ 2.50",
            eta = "Frecuencia cada 4 min",
            badge = "RUTA CONOCIDA",
            badgeColor = Color(0xFF3B82F6)
        ),
        ServiceDetail(
            type = TripType.COMBI,
            icon = Icons.Default.DirectionsBus,
            capacity = "Hasta 16 pasajeros",
            description = "Combis de transporte masivo conectando terminales, avenidas principales y mercados con tarifas populares.",
            basePrice = "Pasaje urbano S/ 1.50 - S/ 2.00",
            eta = "Frecuencia cada 3 min",
            badge = "TRANSPORTE POPULAR",
            badgeColor = Color(0xFFA855F7)
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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("services_back_button")
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
                        text = "Tipos de Servicios",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Modalidades de transporte en Chiclayo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                services.forEach { service ->
                    val isSelected = currentService == service.type
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("service_card_${service.type.name.lowercase()}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(service.badgeColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = service.icon,
                                            contentDescription = null,
                                            tint = service.badgeColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = service.type.displayName,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = service.capacity,
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(service.badgeColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = service.badge,
                                        color = service.badgeColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = service.description,
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = service.basePrice,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = service.eta,
                                    fontSize = 11.5.sp,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onSelectService(service.type) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("select_service_${service.type.name.lowercase()}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Servicio Seleccionado", fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Seleccionar ${service.type.displayName}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
