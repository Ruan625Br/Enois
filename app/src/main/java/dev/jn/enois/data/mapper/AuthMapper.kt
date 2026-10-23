package dev.jn.enois.data.mapper

import dev.jn.enois.data.remote.dto.auth.AuthConfigDto
import dev.jn.enois.data.remote.dto.auth.AuthResponse
import dev.jn.enois.domain.model.AuthConfig
import dev.jn.enois.domain.model.AuthSession

fun AuthResponse.toDomain(): AuthSession =
    AuthSession(
        user = user.toDomain(),
        accessToken = token,
        refreshToken = refreshToken,
        expiresAt = expiresAt,
        refreshExpiresAt = refreshExpiresAt
    )

fun AuthConfigDto.toDomain() = AuthConfig(
    captchaMode = captchaMode,
    turnstileSiteKey = turnstileSiteKey,
    googleLoginEnabled = googleLoginEnabled
)