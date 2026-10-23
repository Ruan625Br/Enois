package dev.jn.enois.domain.model

data class AuthConfig(
    val captchaMode: String,
    val turnstileSiteKey: String? = null,
    val googleLoginEnabled: Boolean = false
)