package dev.jn.enois.domain.model

data class Comment(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String?,
    val authorUsername: String?,
    val authorBadges: List<String>,
    val authorAvatarFrame: AvatarFrameType?,
    val content: String,
    val createdAt: String,
    val parentCommentId: String?,
    val pinned: Boolean,
)