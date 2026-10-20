package dev.jn.enois.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val acceptedTerms: Boolean
)