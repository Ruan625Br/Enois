package dev.jn.enois.domain.model

data class ReactionList(
    val postId: String,
    val summary: ReactionSummary,
    val reactions: List<ReactionUser>,
    val partial: Boolean
)

data class ReactionUser(
    val userId: String,
    val type: ReactionType,
    val createdAt: String
)