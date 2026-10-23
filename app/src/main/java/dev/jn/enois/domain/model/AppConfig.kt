package dev.jn.enois.domain.model

data class AppConfig(
    val apiBaseUrl: String,
    val webOrigin: String
) {
    val turnstileBaseUrl: String get() = "$webOrigin/"
}