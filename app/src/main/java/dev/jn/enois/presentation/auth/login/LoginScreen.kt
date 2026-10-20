package dev.jn.enois.presentation.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginRoute(
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {
    LoginScreen(
        onNavigateToRegister = onNavigateToRegister,
        onLogin = viewModel::login,
        modifier = modifier
    )
}

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLogin: (email: String, password: String) -> Unit,
    modifier: Modifier = Modifier
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )


        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )


        Button(
            onClick = { onLogin(email, password) },
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Login")
        }
        Button(
            onClick = onNavigateToRegister
        ) {
            Text("Register")
        }

    }
}