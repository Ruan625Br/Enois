package dev.jn.enois.domain.model

data class AuthSession(
    val user: User,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)
