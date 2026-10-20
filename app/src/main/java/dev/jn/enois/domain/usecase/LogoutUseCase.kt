package dev.jn.enois.domain.usecase

import dev.jn.enois.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logout()
}