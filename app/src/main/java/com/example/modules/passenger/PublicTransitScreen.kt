package com.example.modules.passenger

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
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.theme.GoldPrimary

data class TransitLineItem(
    val numero: String,
    val tipo: String, // Combi, Colectivo, Autobús
    val nombre: String,
    val puntoInicial: String,
    val puntoFinal: String,
    val horario: String,
    val frecuencia: String,
    val paraderosClave: List<String>,
    val tarifa: String,
    val esRecomendada: Boolean = false
)

@Composable
fun PublicTransitScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todas") }

    val lines = listOf(
        TransitLineItem(
            numero = "Línea 12",
            tipo = "Colectivo Troncal",
            nombre = "Troncal Chiclayo Metropolitano",
            puntoInicial = "Pimentel (Malecón Grau)",
            puntoFinal = "José Leonardo Ortiz (Av. Chiclayo)",
            horario = "05:30 am - 10:45 pm",
            frecuencia = "Cada 3 a 5 min",
            paraderosClave = listOf("Malecón Pimentel", "Óvalo Santa Victoria", "Av. Bolognesi", "Parque Principal", "Mercado Moshoqueque"),
            tarifa = "S/ 2.50",
            esRecomendada = true
        ),
        TransitLineItem(
            numero = "Línea 08",
            tipo = "Combi Urbana",
            nombre = "Ramal Santa Victoria - Mall Aventura",
            puntoInicial = "Urb. Santa Victoria",
            puntoFinal = "Mall Aventura Panamericana",
            horario = "06:00 am - 10:30 pm",
            frecuencia = "Cada 5 min",
            paraderosClave = listOf("Av. La Libertad", "Av. Chinchaysuyo", "Av. Balta Norte", "Hospital Regional", "Mall Aventura"),
            tarifa = "S/ 1.80",
            esRecomendada = true
        ),
        TransitLineItem(
            numero = "Línea 10",
            tipo = "Combi Urbana",
            nombre = "Circuito Lambayeque Centro",
            puntoInicial = "Parque Principal Chiclayo",
            puntoFinal = "Plaza de Armas Lambayeque",
            horario = "05:00 am - 11:00 pm",
            frecuencia = "Cada 4 min",
            paraderosClave = listOf("Av. Balta", "Óvalo El Pescador", "Panamericana Norte", "Universidad UNPRG", "Museo Brüning"),
            tarifa = "S/ 2.00"
        ),
        TransitLineItem(
            numero = "Línea Monsefú",
            tipo = "Colectivo",
            nombre = "Ruta Campiña y Artesanía Monsefú",
            puntoInicial = "Terminal Ormeño / Bolognesi",
            puntoFinal = "Parque Principal Monsefú",
            horario = "06:00 am - 09:30 pm",
            frecuencia = "Cada 6 min",
            paraderosClave = listOf("Av. Bolognesi", "Cruce Reque", "Entrada Monsefú", "Mercado Artesanal"),
            tarifa = "S/ 2.80"
        )
    )

    val filteredLines = lines.filter {
        (selectedFilter == "Todas" || it.tipo.contains(selectedFilter, ignoreCase = true)) &&
                (searchQuery.isBlank() || it.nombre.contains(searchQuery, ignoreCase = true) || it.numero.contains(searchQuery, ignoreCase = true) || it.puntoFinal.contains(searchQuery, ignoreCase = true))
    }

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
                    modifier = Modifier.testTag("public_transit_back_button")
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
                        text = "Transporte Público",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Líneas, combis, colectivos y paraderos de Chiclayo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Search destination box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar destino, línea o paradero...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("transit_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                )
            }

            // Category filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Todas", "Colectivo", "Combi").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("transit_filter_${filter.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Lines list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredLines) { line ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transit_line_card_${line.numero.lowercase().replace(" ", "_")}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (line.esRecomendada) 1.5.dp else 1.dp,
                            color = if (line.esRecomendada) GoldPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GoldPrimary)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = line.numero,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            color = Color.Black
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = line.tipo,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                if (line.esRecomendada) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Recomendada", color = Color(0xFF22C55E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = line.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Route path
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${line.puntoInicial} ➔ ${line.puntoFinal}",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(line.frecuencia, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("Tarifa: ${line.tarifa}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Paraderos preview
                            Text(
                                text = "Paraderos clave: " + line.paraderosClave.joinToString(" • "),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
