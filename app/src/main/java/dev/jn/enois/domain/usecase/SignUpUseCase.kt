package dev.jn.enois.domain.usecase

import dev.jn.enois.domain.repository.AuthRepository

class SignUpUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        name: String,
        email: String,
        password: String
    ) = authRepository.signUp(name.trim(), email.trim(), password)

}
