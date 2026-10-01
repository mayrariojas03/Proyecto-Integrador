package com.example.modules.passenger

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.AppTopBar
import com.example.components.InteractiveMapCanvas
import com.example.models.Trip
import com.example.models.TripType
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary

data class PopularDestination(
    val title: String,
    val address: String,
    val price: String,
    val icon: ImageVector
)

@Composable
fun HomeScreen(
    selectedService: TripType,
    pickupLocation: String,
    destinationInput: String,
    activeTrip: Trip?,
    isSearching: Boolean,
    isDarkTheme: Boolean,
    onMenuClick: () -> Unit,
    onThemeToggleClick: () -> Unit,
    onSelectService: (TripType) -> Unit,
    onDestinationSelect: (String, Double) -> Unit,
    onRequestRideClick: () -> Unit,
    onCancelTrip: () -> Unit,
    onSimulateDriverArrived: () -> Unit,
    onViewFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchDestinationText by remember { mutableStateOf(destinationInput) }
    var offerPriceText by remember { mutableStateOf("7.00") }
    var showOfferModal by remember { mutableStateOf(false) }

    val popularDestinations = listOf(
        PopularDestination(
            title = "Mall Aventura Chiclayo",
            address = "Panamericana Norte & Av. Panamericana",
            price = "S/ 7.00~",
            icon = Icons.Default.ShoppingBag
        ),
        PopularDestination(
            title = "Plaza de Armas de Chiclayo",
            address = "Centro Histórico / Catedral de Chiclayo",
            price = "S/ 5.00~",
            icon = Icons.Default.LocationCity
        ),
        PopularDestination(
            title = "Hospital Regional de Lambayeque",
            address = "Av. Vía de Evitamiento Norte",
            price = "S/ 6.50~",
            icon = Icons.Default.LocalHospital
        ),
        PopularDestination(
            title = "Real Plaza Chiclayo",
            address = "Av. Bolognesi / Francisco Bolognesi",
            price = "S/ 5.50~",
            icon = Icons.Default.ShoppingBag
        ),
        PopularDestination(
            title = "Aeropuerto Cap. FAP José Quiñones",
            address = "Av. Bolognesi s/n, Chiclayo",
            price = "S/ 8.50~",
            icon = Icons.Default.Place
        )
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Layer 1: Vector City Map Background
        InteractiveMapCanvas(
            modifier = Modifier.fillMaxSize(),
            isDark = isDarkTheme
        )

        // Layer 2: Main UI Controls Over Map
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            AppTopBar(
                onMenuClick = onMenuClick,
                onThemeToggleClick = onThemeToggleClick
            )

            // Pickup Location Floating Chip (Av. Balta 450 - Chiclayo Centro)
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable { /* Change pickup */ }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("pickup_location_chip"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "PUNTO DE PARTIDA",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = pickupLocation,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Cambiar punto",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Layer 3: Bottom Floating Card with Service Pills, Search, Popular Destinations & CTA
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                tonalElevation = 10.dp,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // Service Type Selector (4 Pills: Taxi Smart, Mototaxi, Colectivo, Combi)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ServiceOptionItem(
                            title = "Taxi Smart",
                            badge = "RÁPIDO",
                            icon = Icons.Default.LocalTaxi,
                            badgeColor = GoldPrimary,
                            isSelected = selectedService == TripType.TAXI_SMART,
                            onClick = { onSelectService(TripType.TAXI_SMART) },
                            modifier = Modifier.weight(1f)
                        )
                        ServiceOptionItem(
                            title = "Mototaxi",
                            badge = "ECONÓMICO",
                            icon = Icons.Default.TwoWheeler,
                            badgeColor = Color(0xFF22C55E),
                            isSelected = selectedService == TripType.MOTOTAXI,
                            onClick = { onSelectService(TripType.MOTOTAXI) },
                            modifier = Modifier.weight(1f)
                        )
                        ServiceOptionItem(
                            title = "Colectivo",
                            badge = "RUTA FIJA",
                            icon = Icons.Default.Group,
                            badgeColor = Color(0xFF3B82F6),
                            isSelected = selectedService == TripType.COLECTIVO,
                            onClick = { onSelectService(TripType.COLECTIVO) },
                            modifier = Modifier.weight(1f)
                        )
                        ServiceOptionItem(
                            title = "Combi",
                            badge = "POPULAR",
                            icon = Icons.Default.DirectionsBus,
                            badgeColor = Color(0xFFA855F7),
                            isSelected = selectedService == TripType.COMBI,
                            onClick = { onSelectService(TripType.COMBI) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Destination Search Bar: "¿A dónde vas y cuánto ofreces?"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clickable { showOfferModal = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("destination_search_bar")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (destinationInput.isNotBlank()) destinationInput else "¿A dónde vas y cuánto ofreces?",
                                color = if (destinationInput.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                                fontWeight = if (destinationInput.isNotBlank()) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Mi ubicación",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Popular Destinations Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DESTINOS POPULARES EN CHICLAYO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = "Ver favoritos",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldPrimary,
                            modifier = Modifier.clickable { onViewFavoritesClick() }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Popular Destinations Quick List (Limited height for elegance)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        popularDestinations.take(3).forEach { dest ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable {
                                        val priceVal = dest.price.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 7.0
                                        onDestinationSelect(dest.title, priceVal)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                                    .testTag("destination_item_${dest.title.take(6).lowercase()}"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = dest.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = dest.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = dest.address,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = dest.price,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GoldPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom Primary CTA Button: "Solicitar Taxi Smart en Chiclayo →"
                    Button(
                        onClick = onRequestRideClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("solicitar_taxi_smart_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Solicitar ${selectedService.displayName} en Chiclayo",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Active Trip Card Overlay (When Driver Assigned)
        if (activeTrip != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 12.dp,
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Conductor en camino • 3 min",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF22C55E),
                                    fontSize = 13.sp
                                )
                            }

                            IconButton(
                                onClick = onCancelTrip,
                                modifier = Modifier.size(28.dp).testTag("cancel_active_trip_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancelar",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Driver Info Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF261E0A))
                                    .border(1.5.dp, GoldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CM",
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeTrip.nombreConductor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${activeTrip.vehiculoConductor} • ${activeTrip.placaConductor}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${activeTrip.calificacionConductor} ★",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Tarifa: S/ ${String.format("%.2f", activeTrip.precio)}",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Call & Message buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary)
                                        .clickable { /* Call */ },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "Llamar",
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { /* Chat */ },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "Mensaje",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Searching Driver Animation Modal
        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { /* block taps */ }
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = GoldPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Buscando conductor cercano...",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Conectando con unidades en Av. Balta y alrededores de Chiclayo.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = onSimulateDriverArrived,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("simulate_driver_assign_button")
                        ) {
                            Text("Asignar conductor (Demo)", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(onClick = onCancelTrip) {
                            Text("Cancelar búsqueda", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Destination and Offer Customization Modal
        if (showOfferModal) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showOfferModal = false },
                title = {
                    Text(
                        text = "¿A dónde vas y cuánto ofreces?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = searchDestinationText,
                            onValueChange = { searchDestinationText = it },
                            label = { Text("Destino en Chiclayo") },
                            placeholder = { Text("Ej. Real Plaza, Pimentel, UTP...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("modal_destination_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = offerPriceText,
                            onValueChange = { offerPriceText = it },
                            label = { Text("Tu oferta de tarifa (S/)") },
                            placeholder = { Text("7.00") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("modal_offer_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val price = offerPriceText.toDoubleOrNull() ?: 7.0
                            val dest = if (searchDestinationText.isNotBlank()) searchDestinationText else "Plaza de Armas"
                            onDestinationSelect(dest, price)
                            showOfferModal = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Establecer ruta", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showOfferModal = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}

@Composable
fun ServiceOptionItem(
    title: String,
    badge: String,
    icon: ImageVector,
    badgeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .testTag("service_option_${title.lowercase().replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Badge tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor.copy(alpha = 0.2f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = badgeColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
