package dev.jn.enois.domain.usecase

import dev.jn.enois.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ) = authRepository.login(email.trim(), password)

}