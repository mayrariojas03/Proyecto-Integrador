package com.example.models

enum class PaymentType(val displayName: String, val iconRes: String) {
    EFECTIVO("Efectivo", "money"),
    YAPE("Yape", "qr_code"),
    PLIN("Plin", "phone_iphone"),
    SMARTPAY("SmartPay Saldo", "account_balance_wallet"),
    TARJETA("Tarjeta Débito/Crédito", "credit_card")
}

data class PaymentTransaction(
    val id: String,
    val concepto: String,
    val fecha: String,
    val hora: String,
    val monto: Double,
    val metodo: PaymentType,
    val estado: String = "Completado"
)
