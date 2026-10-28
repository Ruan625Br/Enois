package dev.jn.enois.data.remote.api

import dev.jn.enois.data.remote.dto.auth.AuthConfigDto
import dev.jn.enois.data.remote.dto.auth.AuthResponse
import dev.jn.enois.data.remote.dto.auth.LoginRequest
import dev.jn.enois.data.remote.dto.auth.RegisterRequest
import dev.jn.enois.data.remote.dto.auth.UserDto
import dev.jn.enois.data.remote.error.bodyOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import java.util.UUID

class AuthApi(
    private val client: HttpClient
) {

    suspend fun getConfig(): AuthConfigDto = client.get("auth/config").body()

    suspend fun login(
        request: LoginRequest
    ): AuthResponse {
        val response = client.post("auth/login") {
            header("Idempotency-Key", UUID.randomUUID().toString())
            setBody(request)
        }

        return response.bodyOrThrow("Falha ao realizar login.")
    }

    suspend fun signUp(request: RegisterRequest): AuthResponse {
        val response = client.post("auth/signup") {
            header("Idempotency-Key", UUID.randomUUID().toString())
            setBody(request)
        }
        return response.bodyOrThrow("Falha ao realizar registro.")
    }

    suspend fun getMyProfile(): UserDto {
        val response = client.get("auth/me")

        return response.bodyOrThrow("Falha ao obter perfil do usuário.")
    }

    suspend fun refreshToken(): AuthResponse {
        val response = client.post("auth/refresh")
        return response.bodyOrThrow("Falha ao atualizar token.")
    }

    suspend fun logout() {
        val response = client.post("auth/logout")
       return response.bodyOrThrow("Falha ao realizar logout.")
    }
}