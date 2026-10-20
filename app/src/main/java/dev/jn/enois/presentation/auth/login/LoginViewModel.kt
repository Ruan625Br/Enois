package dev.jn.enois.presentation.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jn.enois.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = loginUseCase(email, password)

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
}