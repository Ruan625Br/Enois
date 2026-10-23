package dev.jn.enois.presentation.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jn.enois.domain.repository.AuthRepository
import dev.jn.enois.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadConfig()
    }

    fun login(turnstileToken: String, email: String, password: String) {
        viewModelScope.launch {
            val result = loginUseCase(turnstileToken, email, password)

            result.fold(
                onSuccess = { session ->
                    Log.d("LoginViewModel", "Login realizado com sucesso: ${session.user.name}")
                },
                onFailure = { error ->
                    Log.e("LoginViewModel", "Erro ao realizar login", error)
                }
            )
        }
    }

    private fun loadConfig() = viewModelScope.launch {
        repository.getConfig()
            .onSuccess {
                _uiState.value = _uiState.value.copy(
                    config = it
                )
            }
            .onFailure {
                Log.e("LoginViewModel", "Erro ao carregar configuração", it)
            }
    }
}