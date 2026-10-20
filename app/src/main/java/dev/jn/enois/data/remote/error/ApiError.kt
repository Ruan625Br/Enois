package dev.jn.enois.data.remote.error

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: String? = null,
    val message: String? = null,
    val timestamp: String? = null
)

