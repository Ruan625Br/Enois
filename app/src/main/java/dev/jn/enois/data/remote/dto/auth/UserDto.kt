package dev.jn.enois.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val name: String,
    val headline: String?,
    val email: String,
    val emailVerified: Boolean,
    val twoFactorEnabled: Boolean,
    val identityVerified: Boolean,
    val hasPassword: Boolean
)