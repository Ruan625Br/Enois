package dev.jn.enois.domain.model

data class FeedPage(
    val posts: List<FeedPost>,
    val nextCursor: String?
)
