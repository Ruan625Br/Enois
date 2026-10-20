package dev.jn.enois.data.mapper

import dev.jn.enois.data.remote.dto.auth.UserDto
import dev.jn.enois.domain.model.User

fun UserDto.toDomain() = User(
    id = id,
    username = username,
    name = name,
    headline = headline,
    email = email,
    emailVerified = emailVerified,
    twoFactorEnabled = twoFactorEnabled,
    identityVerified = identityVerified,
    hasPassword = hasPassword
)