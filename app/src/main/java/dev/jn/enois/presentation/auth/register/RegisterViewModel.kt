package dev.jn.enois.presentation.auth.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jn.enois.domain.usecase.SignUpUseCase
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val signUpUseCase: SignUpUseCase
): ViewModel() {

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = signUpUseCase(name, email, password)

            result.fold(
                onSuccess = { session ->
                    Log.d("RegisterViewModel", "Registro realizado com sucesso: ${session.user.name}")
                },
                onFailure = { error ->
                    Log.e("RegisterViewModel", "Erro ao realizar Registro", error)
                }
            )
        }
    }
}