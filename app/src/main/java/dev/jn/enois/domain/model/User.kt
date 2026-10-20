package dev.jn.enois.domain.model

data class User(
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