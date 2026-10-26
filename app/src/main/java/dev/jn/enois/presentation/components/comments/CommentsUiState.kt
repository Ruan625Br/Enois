package dev.jn.enois.presentation.components.comments

import dev.jn.enois.domain.model.Comment

data class CommentsUiState(
    val postId: String? = null,
    val comments: List<Comment> = emptyList(),
    val nextCursor: String? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isSending: Boolean = false,
    val replyingTo: Comment? = null,
    val error: String? = null,
    val sendError: String? = null,
)

sealed interface CommentsEvent {
    data class Sent(val postId: String) : CommentsEvent
}