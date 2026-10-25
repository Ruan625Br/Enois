package dev.jn.enois.domain.model

data class CommentPage(
    val comments: List<Comment>,
    val nextCursor: String?,
)
