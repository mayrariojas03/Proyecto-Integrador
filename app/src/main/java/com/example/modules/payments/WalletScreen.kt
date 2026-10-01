package com.example.modules.payments

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.models.PaymentTransaction
import com.example.models.PaymentType
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary

@Composable
fun WalletScreen(
    balance: Double,
    preferredMethod: PaymentType,
    transactions: List<PaymentTransaction>,
    onSelectPreferredMethod: (PaymentType) -> Unit,
    onTopUpBalance: (Double) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTopUpDialog by remember { mutableStateOf(false) }
    var topUpAmountText by remember { mutableStateOf("20.00") }
    var showAddCardDialog by remember { mutableStateOf(false) }
    var cardMaskedNumber by remember { mutableStateOf("•••• •••• •••• 4242") }

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
                    modifier = Modifier.testTag("wallet_back_button")
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
                        text = "Billetera & SmartPay",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Medios de pago y saldo digital",
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Balance Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SALDO DISPONIBLE SMARTPAY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary,
                                    letterSpacing = 0.8.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "S/ ${String.format("%.2f", balance)}",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showTopUpDialog = true },
                                    modifier = Modifier.weight(1f).height(44.dp).testTag("wallet_top_up_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldPrimary,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Recargar saldo", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Payment Methods
                item {
                    Text(
                        text = "MÉTODOS DE PAGO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.6.sp
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // 1. SmartPay
                            PaymentMethodRow(
                                title = "SmartPay Saldo",
                                subtitle = "Pago rápido sin contacto",
                                icon = Icons.Default.AccountBalanceWallet,
                                isPreferred = preferredMethod == PaymentType.SMARTPAY,
                                onClick = { onSelectPreferredMethod(PaymentType.SMARTPAY) }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // 2. Yape
                            PaymentMethodRow(
                                title = "Yape",
                                subtitle = "Código QR y número celular",
                                icon = Icons.Default.QrCode2,
                                isPreferred = preferredMethod == PaymentType.YAPE,
                                onClick = { onSelectPreferredMethod(PaymentType.YAPE) }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // 3. Plin
                            PaymentMethodRow(
                                title = "Plin",
                                subtitle = "Transferencia instantánea BBVA, Interbank, Scotiabank",
                                icon = Icons.Default.PhoneIphone,
                                isPreferred = preferredMethod == PaymentType.PLIN,
                                onClick = { onSelectPreferredMethod(PaymentType.PLIN) }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // 4. Efectivo
                            PaymentMethodRow(
                                title = "Efectivo",
                                subtitle = "Pago directo al conductor al finalizar",
                                icon = Icons.Default.LocalAtm,
                                isPreferred = preferredMethod == PaymentType.EFECTIVO,
                                onClick = { onSelectPreferredMethod(PaymentType.EFECTIVO) }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // 5. Tarjeta
                            PaymentMethodRow(
                                title = "Tarjeta Visa / Mastercard",
                                subtitle = cardMaskedNumber,
                                icon = Icons.Default.CreditCard,
                                isPreferred = preferredMethod == PaymentType.TARJETA,
                                onClick = { onSelectPreferredMethod(PaymentType.TARJETA) }
                            )
                        }
                    }
                }

                // Add Card Action
                item {
                    Button(
                        onClick = { showAddCardDialog = true },
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("add_card_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Vincular nueva tarjeta", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Transaction History Header
                item {
                    Text(
                        text = "HISTORIAL DE MOVIMIENTOS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.6.sp
                    )
                }

                items(transactions) { tx ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                Text(
                                    text = tx.concepto,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${tx.fecha}, ${tx.hora} • ${tx.metodo.displayName}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "S/ ${String.format("%.2f", tx.monto)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (tx.concepto.contains("Recarga")) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Top up dialog
        if (showTopUpDialog) {
            AlertDialog(
                onDismissRequest = { showTopUpDialog = false },
                title = { Text("Recargar saldo SmartPay") },
                text = {
                    Column {
                        Text("Elige el monto a recargar vía Yape, Plin o Tarjeta:")
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = topUpAmountText,
                            onValueChange = { topUpAmountText = it },
                            label = { Text("Monto en Soles (S/)") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("top_up_amount_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amount = topUpAmountText.toDoubleOrNull() ?: 20.0
                            onTopUpBalance(amount)
                            showTopUpDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("Confirmar recarga", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTopUpDialog = false }) { Text("Cancelar") }
                }
            )
        }

        // Add card dialog (Tokenized, NO CVV stored)
        if (showAddCardDialog) {
            var rawCardNumber by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddCardDialog = false },
                title = { Text("Vincular tarjeta") },
                text = {
                    Column {
                        Text(
                            text = "Por tu seguridad, los datos son procesados directamente por la pasarela de pagos. Smart Chiclayo nunca almacena tu CVV ni tu número completo.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = rawCardNumber,
                            onValueChange = { if (it.length <= 16) rawCardNumber = it },
                            label = { Text("Número de tarjeta (16 dígitos)") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("card_number_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (rawCardNumber.length >= 4) {
                                cardMaskedNumber = "•••• •••• •••• ${rawCardNumber.takeLast(4)}"
                            }
                            showAddCardDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("Guardar tarjeta", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddCardDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun PaymentMethodRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPreferred: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isPreferred) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = if (isPreferred) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (isPreferred) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GoldPrimary.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("Predeterminado", color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
