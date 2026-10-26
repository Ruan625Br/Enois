package dev.jn.enois.data.remote.dto.comment

import kotlinx.serialization.Serializable

@Serializable
data class CommentPageDto(val items: List<CommentDto>, val nextCursor: String? = null)
