package dev.jn.enois.data.remote.api

import android.util.Log
import dev.jn.enois.data.remote.dto.auth.AuthResponse
import dev.jn.enois.data.remote.dto.auth.LoginRequest
import dev.jn.enois.data.remote.dto.auth.RefreshTokenRequest
import dev.jn.enois.data.remote.dto.auth.RegisterRequest
import dev.jn.enois.data.remote.error.ApiError
import dev.jn.enois.data.remote.error.ApiException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import java.util.UUID

class AuthApi(
    private val client: HttpClient
) {

    suspend fun login(
        request: LoginRequest
    ): AuthResponse {
        val response = client.post("auth/login") {
            header("Idempotency-Key", UUID.randomUUID().toString())
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            val error = runCatching {
                response.body<ApiError>()
            }.getOrNull()

            throw ApiException(
                statusCode = response.status.value,
                errorCode = error?.code,
                message = error?.message ?: "Falha ao realizar login."
            )
        }

        return response.body()
    }

    suspend fun signUp(request: RegisterRequest): AuthResponse {
        val response = client.post("auth/signup") {
            header("Idempotency-Key", UUID.randomUUID().toString())
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            val error = runCatching {
                response.body<ApiError>()
            }.getOrNull()

            throw ApiException(
                statusCode = response.status.value,
                errorCode = error?.code,
                message = error?.message ?: "Falha ao realizar registro."
            )
        }

        return response.body()
    }

    suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest) {
        val result = client.post("auth/refresh") {
            setBody(refreshTokenRequest)
        }.body<String>()
        Log.d("AuthApi", "refreshToken: $result")
    }
}