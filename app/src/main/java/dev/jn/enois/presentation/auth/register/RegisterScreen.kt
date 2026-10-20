package dev.jn.enois.presentation.auth.register

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
fun RegisterRoute(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = koinViewModel()
) {
    RegisterScreen(
        onNavigateToLogin = onNavigateToLogin,
        onRegister = viewModel::register,
        modifier = modifier
    )
}

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegister: (name: String, email: String, password: String) -> Unit,
    modifier: Modifier = Modifier
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

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
            onClick = { onRegister(name, email, password) },
            enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Register")
        }

        Button(
            onClick = onNavigateToLogin
        ) {
            Text("Login")
        }

    }
}