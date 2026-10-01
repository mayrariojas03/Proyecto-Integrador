package com.example.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.models.User
import com.example.state.AppScreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary

@Composable
fun SideMenuDrawer(
    currentUser: User?,
    currentScreen: AppScreen,
    unreadNotifCount: Int = 2,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(scrollState)
        ) {
            // User Profile Header Card (As shown in PDF Page 6)
            val displayName = currentUser?.nombres?.ifBlank { "Mayra" } ?: "Mayra"
            val initial = displayName.firstOrNull()?.uppercase() ?: "M"

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onNavigate(AppScreen.PROFILE) }
                    .padding(14.dp)
                    .testTag("drawer_user_header")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Avatar circle with badge
                    Box(modifier = Modifier.size(46.dp)) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2E2413))
                                .border(1.5.dp, GoldPrimary, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initial,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        // Notification dot
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                                .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (currentUser?.esVip == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GoldPrimary.copy(alpha = 0.2f))
                                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "VIP",
                                        color = GoldPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        val rating = currentUser?.calificacion ?: 0.0
                        val viajes = currentUser?.totalViajes ?: 0
                        Text(
                            text = if (viajes == 0) "★ 0.0 (0 viajes)" else "★ ${String.format("%.1f", rating)} ($viajes viajes)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Ver perfil",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Options (Strictly 1 to 12 as requested in PDF page 6)

            // 1. Perfil del usuario
            DrawerMenuItem(
                icon = Icons.Default.Person,
                title = "Perfil del usuario",
                isSelected = currentScreen == AppScreen.PROFILE,
                onClick = { onNavigate(AppScreen.PROFILE) },
                testTag = "drawer_item_profile"
            )

            // 2. Ciudad (Urban rides)
            DrawerMenuItem(
                icon = Icons.Default.DirectionsCar,
                title = "Ciudad (Urban rides)",
                isSelected = currentScreen == AppScreen.HOME,
                iconTint = GoldPrimary,
                showActiveDot = true,
                onClick = { onNavigate(AppScreen.HOME) },
                testTag = "drawer_item_city"
            )

            // 3. Historial de viajes
            DrawerMenuItem(
                icon = Icons.Default.History,
                title = "Historial de viajes",
                isSelected = currentScreen == AppScreen.TRIP_HISTORY,
                onClick = { onNavigate(AppScreen.TRIP_HISTORY) },
                testTag = "drawer_item_history"
            )

            // 4. Tipos de servicios (Clean, without "4 modos" badge as instructed)
            DrawerMenuItem(
                icon = Icons.Default.GridView,
                title = "Tipos de servicios",
                isSelected = currentScreen == AppScreen.SERVICES_CATALOG,
                onClick = { onNavigate(AppScreen.SERVICES_CATALOG) },
                testTag = "drawer_item_services"
            )

            // 5. Chiclayo a Ciudades
            DrawerMenuItem(
                icon = Icons.Default.Public,
                title = "Chiclayo a Ciudades",
                subtitle = "Pimentel/Lamb.",
                isSelected = currentScreen == AppScreen.INTERCITY,
                onClick = { onNavigate(AppScreen.INTERCITY) },
                testTag = "drawer_item_intercity"
            )

            // 6. Transporte público
            DrawerMenuItem(
                icon = Icons.Default.DirectionsBus,
                title = "Transporte público",
                subtitle = "Líneas y combis",
                isSelected = currentScreen == AppScreen.PUBLIC_TRANSIT,
                onClick = { onNavigate(AppScreen.PUBLIC_TRANSIT) },
                testTag = "drawer_item_public_transit"
            )

            // 7. Billetera & SmartPay
            DrawerMenuItem(
                icon = Icons.Default.AccountBalanceWallet,
                title = "Billetera & SmartPay",
                isSelected = currentScreen == AppScreen.WALLET,
                onClick = { onNavigate(AppScreen.WALLET) },
                testTag = "drawer_item_wallet"
            )

            // 8. Notificaciones
            DrawerMenuItem(
                icon = Icons.Default.Notifications,
                title = "Notificaciones",
                badgeText = if (unreadNotifCount > 0) "$unreadNotifCount" else null,
                isSelected = currentScreen == AppScreen.NOTIFICATIONS,
                onClick = { onNavigate(AppScreen.NOTIFICATIONS) },
                testTag = "drawer_item_notifications"
            )

            // 9. Seguridad y SOS
            DrawerMenuItem(
                icon = Icons.Default.Security,
                title = "Seguridad y SOS",
                isSelected = currentScreen == AppScreen.SECURITY_SOS,
                onClick = { onNavigate(AppScreen.SECURITY_SOS) },
                testTag = "drawer_item_security"
            )

            // 10. Reporte de incidencias (Clean, without "En ruta" badge as instructed)
            DrawerMenuItem(
                icon = Icons.Default.Warning,
                title = "Reporte de incidencias",
                isSelected = currentScreen == AppScreen.INCIDENTS,
                onClick = { onNavigate(AppScreen.INCIDENTS) },
                testTag = "drawer_item_incidents"
            )

            // 11. Configuración
            DrawerMenuItem(
                icon = Icons.Default.Settings,
                title = "Configuración",
                isSelected = currentScreen == AppScreen.SETTINGS,
                onClick = { onNavigate(AppScreen.SETTINGS) },
                testTag = "drawer_item_settings"
            )

            // 12. Ayuda & Soporte 24/7
            DrawerMenuItem(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                title = "Ayuda & Soporte 24/7",
                isSelected = currentScreen == AppScreen.SUPPORT,
                onClick = { onNavigate(AppScreen.SUPPORT) },
                testTag = "drawer_item_support"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // CTA 1: MODO GESTOR DE LÍNEA
            OutlinedButton(
                onClick = { onNavigate(AppScreen.MANAGER_WIZARD) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("drawer_button_gestor"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = GoldPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CorporateFare,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MODO GESTOR DE LÍNEA",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CTA 2: MODO CONDUCTOR
            Button(
                onClick = { onNavigate(AppScreen.DRIVER_PORTAL) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("drawer_button_conductor"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.TwoWheeler,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MODO CONDUCTOR",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer: Chiclayo City branding
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Smart Chiclayo • Lambayeque",
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    isSelected: Boolean = false,
    showActiveDot: Boolean = false,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else iconTint,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(GoldPrimary)
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        } else if (showActiveDot) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary)
            )
        }
    }
}
