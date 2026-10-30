package dev.jn.enois.data.remote.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class AuthorNameStyleDto(
    val theme: String? = null,
    val font: String? = null,
    val color: String? = null,
    val nameplate: String? = null
)