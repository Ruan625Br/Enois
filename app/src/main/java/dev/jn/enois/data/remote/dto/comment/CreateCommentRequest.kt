package dev.jn.enois.data.remote.dto.comment

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequest(
    val content: String,
    val parentCommentId: String? = null,
    val mediaIds: List<String> = emptyList(),
    val mentionedUserIds: List<String> = emptyList(),
    val website: String = "",
    val mediaSpoiler: Boolean = false,
)