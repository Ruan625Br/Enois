package dev.jn.enois.domain.repository

import dev.jn.enois.domain.model.AuthSession

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession>

    suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<AuthSession>

    suspend fun refreshToken(
    ): Result<Unit>

    suspend fun logout(): Result<Unit>

    suspend fun forgotPassword(
        email: String
    ): Result<Unit>

}