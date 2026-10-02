package com.example.modules.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.models.AuthProvider
import com.example.state.AuthTab

@Composable
fun AuthScreen(
    currentTab: AuthTab,
    onTabChange: (AuthTab) -> Unit,
    onLoginWithGoogle: (
        email: String,
        password: String,
        onResult: (String?) -> Unit
    ) -> Unit,
    onLoginWithPhone: (
        phone: String,
        password: String,
        onResult: (String?) -> Unit
    ) -> Unit,
    onRequestPhoneOtp: (String) -> Unit,
    onRegisterSubmit: (
        nombres: String,
        apellidos: String,
        dni: String,
        telefono: String,
        correo: String,
        fechaNacimiento: String,
        password: String,
        onResult: (String?) -> Unit
    ) -> Unit,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    when (currentTab) {

        AuthTab.LOGIN -> {
            LoginScreen(
                onGoogleLoginClick = {
                    onLoginWithGoogle(
                        "",
                        "",
                        {}
                    )
                },
                onSendPhoneCodeClick = { phone ->
                    onRequestPhoneOtp(phone)
                },
                onHelpClick = onHelpClick,
                onTermsClick = onTermsClick,
                onPrivacyClick = onPrivacyClick,
                modifier = modifier
            )
        }

        AuthTab.REGISTER -> {
            RegisterScreen(
                authProvider = AuthProvider.EMAIL,
                onBackClick = {
                    onTabChange(AuthTab.LOGIN)
                },
                onRegisterSuccess = {
                        nombres,
                        apellidos,
                        dni,
                        telefono,
                        correo,
                        fechaNacimiento,
                        onResult ->

                    onRegisterSubmit(
                        nombres,
                        apellidos,
                        dni,
                        telefono,
                        correo,
                        fechaNacimiento,
                        "12345678",
                        onResult
                    )
                },
                modifier = modifier
            )
        }
    }
}