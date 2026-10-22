package dev.jn.enois.data.remote.dto.media

import kotlinx.serialization.Serializable

@Serializable
data class MediaDto(
    val id: String,
    val fileName: String,
    val mimeType: String,
    val size: Int,
    val purpose: String,
    val status: String,
    val uploadUrl: String? = null,
    val cdnUrl: String?,
    val createdAt: String
)