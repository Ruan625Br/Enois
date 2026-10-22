package dev.jn.enois.domain.model


data class Media(
    val id: String,
    val fileName: String,
    val mimeType: String,
    val size: Int,
    val purpose: String,
    val status: String,
    val uploadUrl: String?,
    val cdnUrl: String?,
    val createdAt: String
)