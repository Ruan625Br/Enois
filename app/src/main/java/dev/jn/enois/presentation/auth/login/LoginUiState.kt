package dev.jn.enois.presentation.auth.login

import androidx.compose.runtime.Stable
import dev.jn.enois.domain.model.AuthConfig

@Stable
data class LoginUiState(
    val config: AuthConfig? = null,
)
