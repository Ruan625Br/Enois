package dev.jn.enois.data.remote.dto.comment

import dev.jn.enois.domain.model.AvatarFrameType
import dev.jn.enois.domain.model.Comment
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String? = null,
    val authorUsername: String?,
    val authorBadges: List<String>,
    val authorAvatarFrame: String?,
    val content: String,
    val createdAt: String,
    val parentCommentId: String? = null,
    val pinned: Boolean = false,
)

fun CommentDto.toDomain() =
    Comment(
        id = id,
        authorId = authorId,
        authorName = authorName,
        authorUsername = authorUsername,
        authorBadges = authorBadges,
        authorAvatarFrame = AvatarFrameType.fromFxName(authorAvatarFrame),
        authorAvatar = authorAvatar,
        content = content,
        createdAt = createdAt,
        parentCommentId = parentCommentId,
        pinned = pinned,
    )