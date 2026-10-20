package dev.jn.enois.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
