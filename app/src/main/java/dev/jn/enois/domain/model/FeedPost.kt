package dev.jn.enois.domain.model

import androidx.compose.runtime.Stable

@Stable
data class FeedPost(
    val id: String,
    val authorId: String,
    val authorUsername: String,
    val authorName: String,
    val authorBadge: String?,
    val authorBadges: List<String>?,
    val authorAvatar: String?,
    val authorAvatarFrame: String?,
    val authorIdentity: AuthorIdentity?,
    val type: PostType,
    val content: String,
    val pageHtml: String?,
    val articleBody: String?,
    val tags: List<String>,
    val mediaIds: List<String>,
    val createdAt: String,
    val visibility: PostVisibility,
    val counters: PostCounters,
    val status: PostStatus,
    val currentUserReaction: String?,
    val communityId: String?,
    val community: PostCommunity?,
    val editedAt: String?,
    val expiredAt: String?,
) {
    fun getBadges(): List<String> {
        val badges = authorBadges?.toMutableList() ?: mutableListOf()
        authorBadge?.let { badges.add(it) }
        return badges
    }
}

enum class AuthorIdentity {
    SOCIAL,
    CORPORATE
}

enum class PostType {
    INSIGHT,
    ARTICLE,
    QUESTION,
    PROJECT,
    OPPORTUNITY,
    PAGE,
    NEWS,
    TUTORIAL,
    REVIEW,
    BUSINESS,
    DIVERTIDO,
    MOMENT,
    BLOG,
}

enum class PostVisibility {
    PUBLIC,
    CONNECTIONS,
    MENTORS,
}

enum class PostStatus {
    ACTIVE,
    DELETED
}

data class PostCounters(
    val reactions: Int,
    val comments: Int,
    val saves: Int,
    val shares: Int,
)


data class PostCommunity(
    val id: String,
    val name: String,
    val joined: Boolean
)
