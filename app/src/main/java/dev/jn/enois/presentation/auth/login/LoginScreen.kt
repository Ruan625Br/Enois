package dev.jn.enois.presentation.auth.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.jn.enois.domain.model.AppConfig
import dev.jn.enois.presentation.auth.components.TurnstileComponent
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LoginRoute(
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appConfig = koinInject<AppConfig>()

    LoginScreen(
        uiState = uiState,
        appConfig = appConfig,
        onNavigateToRegister = onNavigateToRegister,
        onLogin = viewModel::login,
        modifier = modifier
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    appConfig: AppConfig,
    onNavigateToRegister: () -> Unit,
    onLogin: (turnstileToken: String, email: String, password: String) -> Unit,
    modifier: Modifier = Modifier
) {

    val needsTurnstile = uiState.config?.captchaMode == "TURNSTILE" &&
            uiState.config.turnstileSiteKey != null
    var turnstileToken by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var attempt by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            label = { Text("Email") },
            shape = MaterialTheme.shapes.large
        )


        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            label = { Text("Password") },
            shape = MaterialTheme.shapes.large
        )

        Spacer(
            modifier = Modifier
                .heightIn(10.dp)
        )

        Button(
            modifier = Modifier
                .width(300.dp),
            onClick = {
                turnstileToken?.let {
                    onLogin(it, email, password)
                    attempt++
                }
            },
            enabled = email.isNotBlank() && password.isNotBlank() &&
                    (!needsTurnstile || turnstileToken != null)
        ) {
            Text("Login")
        }
        TextButton(
            onClick = onNavigateToRegister
        ) {
            Text("Register")
        }


        if (needsTurnstile) {
            key(attempt) {
                TurnstileComponent(
                    siteKey = uiState.config.turnstileSiteKey,
                    baseUrl = appConfig.turnstileBaseUrl,
                    onToken = {
                        turnstileToken = it
                    },
                    onError = {
                        turnstileToken = null
                        Log.e("LoginScreen", "Turnstile error: $it")
                    }
                )
            }
        }
    }
}