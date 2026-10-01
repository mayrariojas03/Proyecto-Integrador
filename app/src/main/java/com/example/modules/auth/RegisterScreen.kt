package com.example.modules.auth

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.AuthProvider
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary

@Composable
fun RegisterScreen(
    initialNombres: String = "",
    initialApellidos: String = "",
    initialCorreo: String = "",
    initialTelefono: String = "",
    authProvider: AuthProvider = AuthProvider.EMAIL,
    onBackClick: () -> Unit,
    onRegisterSuccess: (
        nombres: String,
        apellidos: String,
        dni: String,
        telefono: String,
        correo: String,
        fechaNacimiento: String
    ) -> Boolean,
    modifier: Modifier = Modifier
) {
    var nombres by remember { mutableStateOf(initialNombres) }
    var apellidos by remember { mutableStateOf(initialApellidos) }
    var dni by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf(initialTelefono) }
    var correo by remember { mutableStateOf(initialCorreo) }
    var fechaNacimiento by remember { mutableStateOf("15/05/1998") }
    var password by remember { mutableStateOf("12345678") }
    var confirmPassword by remember { mutableStateOf("12345678") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(true) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0D0E11)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("register_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Registro de usuario",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Smart Chiclayo • Rol Pasajero",
                        fontSize = 12.sp,
                        color = GoldAccent
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 24.dp)
            ) {
                if (authProvider != AuthProvider.EMAIL) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E2419))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Identidad validada mediante ${authProvider.name.lowercase().replaceFirstChar { it.uppercase() }}. Completa tus datos para tu seguridad en cada viaje.",
                                color = Color(0xFFD1FAE5),
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF3B1215))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFFF8B94),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 1. Nombres
                OutlinedTextField(
                    value = nombres,
                    onValueChange = { nombres = it; errorMessage = null },
                    label = { Text("Nombres") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_nombres"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF2C2F3C),
                        focusedLabelColor = GoldPrimary,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Apellidos
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it; errorMessage = null },
                    label = { Text("Apellidos") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_apellidos"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF2C2F3C),
                        focusedLabelColor = GoldPrimary,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. DNI o Documento (8 dígitos)
                OutlinedTextField(
                    value = dni,
                    onValueChange = {
                        if (it.length <= 8) {
                            dni = it
                            errorMessage = null
                        }
                    },
                    label = { Text("DNI o Documento de Identidad (8 dígitos)") },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = GoldPrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_dni"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF2C2F3C),
                        focusedLabelColor = GoldPrimary,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Teléfono (9 dígitos)
                OutlinedTextField(
                    value = telefono,
                    onValueChange = {
                        if (it.length <= 9) {
                            telefono = it
                            errorMessage = null
                        }
                    },
                    label = { Text("Número de teléfono (9 dígitos)") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_telefono"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF2C2F3C),
                        focusedLabelColor = GoldPrimary,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 5. Correo
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it; errorMessage = null },
                    label = { Text("Correo electrónico") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = GoldPrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_correo"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF2C2F3C),
                        focusedLabelColor = GoldPrimary,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 6. Fecha de Nacimiento
                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = { fechaNacimiento = it },
                    label = { Text("Fecha de nacimiento (DD/MM/AAAA)") },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = GoldPrimary)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_nacimiento"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF2C2F3C),
                        focusedLabelColor = GoldPrimary,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 7. Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color(0xFFA0A3B1)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_password"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF2C2F3C),
                        focusedLabelColor = GoldPrimary,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 8. Confirmar Contraseña
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar contraseña") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary)
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_confirm_password"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF2C2F3C),
                        focusedLabelColor = GoldPrimary,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Terms checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { termsAccepted = !termsAccepted },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GoldPrimary,
                            uncheckedColor = Color(0xFF6B7280),
                            checkmarkColor = Color.Black
                        ),
                        modifier = Modifier.testTag("checkbox_terms")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Acepto los Términos de Servicio y la Política de Privacidad de Smart Chiclayo.",
                        color = Color(0xFFD1D5DB),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button: "Crear cuenta"
                Button(
                    onClick = {
                        if (nombres.isBlank() || apellidos.isBlank()) {
                            errorMessage = "Ingresa tus nombres y apellidos completos."
                            return@Button
                        }
                        if (dni.length < 8) {
                            errorMessage = "Ingresa un número de DNI válido de 8 dígitos."
                            return@Button
                        }
                        if (telefono.length < 9) {
                            errorMessage = "Ingresa un número telefónico celular de 9 dígitos."
                            return@Button
                        }
                        if (!correo.contains("@")) {
                            errorMessage = "Ingresa un correo electrónico válido."
                            return@Button
                        }
                        if (password != confirmPassword) {
                            errorMessage = "Las contraseñas no coinciden."
                            return@Button
                        }
                        if (!termsAccepted) {
                            errorMessage = "Debes aceptar los términos y políticas para continuar."
                            return@Button
                        }

                        val success = onRegisterSuccess(
                            nombres.trim(),
                            apellidos.trim(),
                            dni.trim(),
                            telefono.trim(),
                            correo.trim(),
                            fechaNacimiento.trim()
                        )
                        if (!success) {
                            errorMessage = "El DNI, teléfono o correo ya se encuentra registrado."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("create_account_submit_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Crear cuenta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
