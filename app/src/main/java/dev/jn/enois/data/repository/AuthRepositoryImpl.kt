package dev.jn.enois.data.repository

import dev.jn.enois.data.remote.api.AuthApi
import dev.jn.enois.data.remote.dto.auth.LoginRequest
import dev.jn.enois.data.remote.dto.auth.RegisterRequest
import dev.jn.enois.data.remote.mapper.toDomain
import dev.jn.enois.domain.model.AuthSession
import dev.jn.enois.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi
): AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession> = runCatching{
        val response = authApi.login(
            LoginRequest(
                email = email,
                password = password
            )
        )

        val session = AuthSession(
            user = response.user.toDomain(),
            accessToken = response.token,
            refreshToken = response.refreshToken,
            expiresIn = 0L //TODO: Implementar
        )

        //TODO: Salvar a sessão no data store

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

        val session = AuthSession(
            user = response.user.toDomain(),
            accessToken = response.token,
            refreshToken = response.refreshToken,
            expiresIn = 0L //TODO: Implementar
        )

        //TODO: Salvar a sessão no data store

        session

    }

    override suspend fun refreshToken(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}