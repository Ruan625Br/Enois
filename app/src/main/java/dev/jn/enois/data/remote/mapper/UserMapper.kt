package dev.jn.enois.data.remote.mapper

import dev.jn.enois.data.remote.dto.auth.UserDto
import dev.jn.enois.domain.model.User

fun UserDto.toDomain() = User(
    id = id,
    username = username,
    name = name,
    email = email,
)