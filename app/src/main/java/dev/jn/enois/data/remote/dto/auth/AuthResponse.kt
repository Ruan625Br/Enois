package dev.jn.enois.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val expiresAt: String,
    val refreshExpiresAt: String,
    val user: UserDto
)