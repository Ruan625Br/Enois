package dev.jn.enois.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthConfigDto(
    val captchaMode: String,
    val turnstileSiteKey: String? = null,
    val googleLoginEnabled: Boolean = false
)
