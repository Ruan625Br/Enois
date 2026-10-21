package dev.jn.enois.data.remote.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class FeedResponseDto(
    val items: List<FeedPostDto>,
    val nextCursor: String? = null
)
