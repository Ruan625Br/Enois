package dev.jn.enois.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val expiresAt: String,
    val refreshExpiresAt: String,
    val user: UserDto
)