package dev.jn.enois.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val name: String,
    val email: String,
)