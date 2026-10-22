package dev.jn.enois.domain.model


data class ReactionSummary(
    val counts: Map<ReactionType, Int> = emptyMap(),
) {

    operator fun get(type: ReactionType): Int = counts[type] ?: 0


    val totalCount: Int get() = counts.values.sum()
}


data class PostAction(
    val postId: String,
    val status: String,
    val counters: PostCounters
)