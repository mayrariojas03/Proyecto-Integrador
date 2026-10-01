package com.example.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary

@Composable
fun InteractiveMapCanvas(
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    // Pulse animation for GPS vehicle
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    val mapBg = if (isDark) Color(0xFF111318) else Color(0xFFE8ECEF)
    val blockColor = if (isDark) Color(0xFF191C24) else Color(0xFFF4F6F8)
    val parkColor = if (isDark) Color(0xFF13281E) else Color(0xFFD6EADB)
    val secondaryRoad = if (isDark) Color(0xFF262A36) else Color(0xFFD3D8E0)
    val primaryRoad = if (isDark) Color(0xFF33384A) else Color(0xFFBAC2CF)
    val highwayRoad = if (isDark) Color(0xFF434960) else Color(0xFFA5B0C2)

    Box(modifier = modifier.fillMaxSize().background(mapBg)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Draw city blocks and green zones
            drawRect(
                color = blockColor,
                topLeft = Offset(w * 0.05f, h * 0.08f),
                size = androidx.compose.ui.geometry.Size(w * 0.38f, h * 0.22f)
            )
            drawRect(
                color = blockColor,
                topLeft = Offset(w * 0.52f, h * 0.08f),
                size = androidx.compose.ui.geometry.Size(w * 0.43f, h * 0.22f)
            )
            drawRect(
                color = blockColor,
                topLeft = Offset(w * 0.05f, h * 0.38f),
                size = androidx.compose.ui.geometry.Size(w * 0.40f, h * 0.25f)
            )
            drawRect(
                color = blockColor,
                topLeft = Offset(w * 0.54f, h * 0.38f),
                size = androidx.compose.ui.geometry.Size(w * 0.41f, h * 0.25f)
            )

            // Plaza de Armas green park
            drawRoundRect(
                color = parkColor,
                topLeft = Offset(w * 0.55f, h * 0.42f),
                size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.16f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )

            // 2. Secondary grid streets
            for (i in 1..8) {
                val y = h * (0.08f + i * 0.08f)
                drawLine(
                    color = secondaryRoad,
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 6f
                )
            }
            for (i in 1..6) {
                val x = w * (0.12f + i * 0.14f)
                drawLine(
                    color = secondaryRoad,
                    start = Offset(x, 0f),
                    end = Offset(x, h),
                    strokeWidth = 6f
                )
            }

            // 3. Primary Chiclayo Avenues
            // Av. José Balta (diagonal arterial crossing central Chiclayo)
            val baltaPath = Path().apply {
                moveTo(w * 0.20f, 0f)
                lineTo(w * 0.50f, h * 0.45f)
                lineTo(w * 0.85f, h)
            }
            drawPath(
                path = baltaPath,
                color = primaryRoad,
                style = Stroke(width = 24f, cap = StrokeCap.Round)
            )

            // Av. Salaverry (horizontal transit corridor)
            drawLine(
                color = primaryRoad,
                start = Offset(0f, h * 0.34f),
                end = Offset(w, h * 0.34f),
                strokeWidth = 20f
            )

            // Av. Bolognesi
            drawLine(
                color = primaryRoad,
                start = Offset(0f, h * 0.68f),
                end = Offset(w, h * 0.68f),
                strokeWidth = 22f
            )

            // Panamericana Norte (highway on edge)
            val panamPath = Path().apply {
                moveTo(w * 0.92f, 0f)
                quadraticTo(w * 0.88f, h * 0.40f, w * 0.95f, h)
            }
            drawPath(
                path = panamPath,
                color = highwayRoad,
                style = Stroke(width = 28f, cap = StrokeCap.Round)
            )

            // 4. Active Route Polyline (Gold glowing transit line)
            val routePath = Path().apply {
                moveTo(w * 0.48f, h * 0.42f) // Pickup: Av. Balta
                lineTo(w * 0.50f, h * 0.45f)
                lineTo(w * 0.68f, h * 0.58f)
                lineTo(w * 0.76f, h * 0.68f) // Toward destination
            }
            drawPath(
                path = routePath,
                color = GoldPrimary.copy(alpha = 0.4f),
                style = Stroke(width = 16f, cap = StrokeCap.Round)
            )
            drawPath(
                path = routePath,
                color = GoldPrimary,
                style = Stroke(
                    width = 8f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 15f), 0f)
                )
            )

            // 5. Destination Pin
            val destX = w * 0.76f
            val destY = h * 0.68f
            drawCircle(
                color = Color(0xFFEF4444),
                radius = 12f,
                center = Offset(destX, destY)
            )
            drawCircle(
                color = Color.White,
                radius = 5f,
                center = Offset(destX, destY)
            )

            // 6. User / Pickup Location GPS Pin (Av. Balta 450)
            val pickupX = w * 0.48f
            val pickupY = h * 0.42f

            // Pulse ring
            drawCircle(
                color = GoldPrimary.copy(alpha = pulseAlpha),
                radius = pulseRadius,
                center = Offset(pickupX, pickupY)
            )

            // Center location dot
            drawCircle(
                color = GoldAccent,
                radius = 14f,
                center = Offset(pickupX, pickupY)
            )
            drawCircle(
                color = Color(0xFF0D0E11),
                radius = 7f,
                center = Offset(pickupX, pickupY)
            )
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = Offset(pickupX, pickupY)
            )

            // 7. Nearby available Smart Taxis (cars roaming Chiclayo)
            val taxi1 = Offset(w * 0.32f, h * 0.28f)
            val taxi2 = Offset(w * 0.62f, h * 0.34f)
            val taxi3 = Offset(w * 0.42f, h * 0.60f)

            listOf(taxi1, taxi2, taxi3).forEach { pos ->
                drawCircle(
                    color = Color.Black.copy(alpha = 0.5f),
                    radius = 9f,
                    center = Offset(pos.x, pos.y + 2f)
                )
                drawCircle(
                    color = GoldPrimary,
                    radius = 8f,
                    center = pos
                )
                drawCircle(
                    color = Color.Black,
                    radius = 3f,
                    center = pos
                )
            }
        }
    }
}
