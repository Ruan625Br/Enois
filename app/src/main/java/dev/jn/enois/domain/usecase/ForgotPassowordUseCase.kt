package dev.jn.enois.domain.usecase

import dev.jn.enois.domain.repository.AuthRepository

class ForgotPasswordUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        email: String
    ) = authRepository.forgotPassword(email.trim())
}