package dev.jn.enois.data.repository

import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.AuthApi
import dev.jn.enois.data.remote.dto.auth.LoginRequest
import dev.jn.enois.data.remote.dto.auth.RegisterRequest
import dev.jn.enois.domain.model.AuthConfig
import dev.jn.enois.domain.model.AuthSession
import dev.jn.enois.domain.repository.AuthRepository
import dev.jn.enois.domain.repository.SessionRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionRepository: SessionRepository
) : AuthRepository {

    override suspend fun getConfig(): Result<AuthConfig> = runCatching {
        authApi.getConfig().toDomain()
    }

    override suspend fun login(
        turnstileToken: String,
        email: String,
        password: String
    ): Result<AuthSession> = runCatching {
        val response = authApi.login(
            LoginRequest(
                email = email,
                password = password,
                turnstileToken = turnstileToken
            )
        )

        val session = response.toDomain()

        sessionRepository.updateSession(session)

        session
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<AuthSession> = runCatching {
        val response = authApi.signUp(
            RegisterRequest(
                name = name,
                email = email,
                password = password,
                acceptedTerms = true
            )
        )

        val session = response.toDomain()

        sessionRepository.updateSession(session)

        session

    }

    override suspend fun refreshToken(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Result<Unit> {
        sessionRepository.logout()

        return Result.success(Unit)
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}