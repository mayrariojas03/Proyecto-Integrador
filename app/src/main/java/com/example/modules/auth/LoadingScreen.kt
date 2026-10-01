package com.example.modules.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onLoadingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stepIndex by remember { mutableIntStateOf(0) }

    val steps = listOf(
        "Validando sesión activa...",
        "Cargando datos del usuario...",
        "Identificando perfil de pasajero...",
        "Cargando preferencias y tema...",
        "Preparando ubicación satelital Chiclayo...",
        "Revisando notificaciones pendientes...",
        "¡Todo listo! Bienvenido a Smart Chiclayo"
    )

    // Sequential step progression and auto-navigation to Home
    LaunchedEffect(Unit) {
        for (i in 0 until steps.size) {
            stepIndex = i
            delay(450)
        }
        delay(350)
        onLoadingComplete()
    }

    // Car linear moving animation across screen
    val infiniteTransition = rememberInfiniteTransition(label = "car_linear_anim")
    val carOffsetFraction by infiniteTransition.animateFloat(
        initialValue = -0.45f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "carOffsetFraction"
    )

    Surface(
        modifier = modifier.fillMaxSize().testTag("app_loading_screen"),
        color = Color(0xFF0C0D11)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Main Brand Title & Tagline (Matching PDF Page 4)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Smart\nChiclayo",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 46.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Chiclayo se mueve con distinción.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFC7CBD8),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Linear Moving Car Animation Track (As requested in PDF Page 4)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Asphalt road line with dashed markings
                    Canvas(modifier = Modifier.fillMaxWidth().height(16.dp)) {
                        val w = size.width
                        val h = size.height
                        // Road base
                        drawLine(
                            color = Color(0xFF232631),
                            start = Offset(0f, h / 2),
                            end = Offset(w, h / 2),
                            strokeWidth = 8f
                        )
                        // Glowing center lane
                        drawLine(
                            color = GoldPrimary.copy(alpha = 0.4f),
                            start = Offset(0f, h / 2),
                            end = Offset(w, h / 2),
                            strokeWidth = 2f
                        )
                    }

                    // Linear moving taxi car icon container
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = (carOffsetFraction * 400).toInt(),
                                    y = -12
                                )
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(GoldPrimary, GoldAccent)
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = "Taxi Smart Chiclayo",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Progress Step Text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF161820))
                        .border(1.dp, Color(0xFF2C2F3C), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = GoldPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = steps.getOrElse(stepIndex) { "Cargando experiencia..." },
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Bottom Brand Footnote
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Powered by Rimac & Porsche Design",
                    color = Color(0xFF6B7280),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
