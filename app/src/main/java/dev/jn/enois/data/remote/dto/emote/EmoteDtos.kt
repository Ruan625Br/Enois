package dev.jn.enois.data.remote.dto.emote

import kotlinx.serialization.Serializable

@Serializable
data class EmoteIndexDto(val entries: Map<String, String> = emptyMap())

@Serializable
data class EmoteItemDto(
    val code: String,
    val animated: Boolean = false,
    val image: String,
    val thumb: String,
)

@Serializable
data class EmotePageDto(
    val items: List<EmoteItemDto> = emptyList(),
    val page: Int,
    val totalPages: Int,
    val total: Int = 0,
)