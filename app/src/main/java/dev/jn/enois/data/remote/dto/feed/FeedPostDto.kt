package dev.jn.enois.data.remote.dto.feed

import dev.jn.enois.domain.model.AuthorIdentity
import dev.jn.enois.domain.model.PostStatus
import dev.jn.enois.domain.model.PostType
import dev.jn.enois.domain.model.PostVisibility
import kotlinx.serialization.Serializable

@Serializable
data class FeedPostDto(
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
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val counters: PostCountersDto,
    val status: PostStatus = PostStatus.ACTIVE,
    val currentUserReaction: String?,
    val communityId: String?,
    val community: PostCommunityDto?,
    val editedAt: String?,
    val expiredAt: String? = null,
)



@Serializable
data class PostCountersDto(
    val reactions: Int,
    val comments: Int,
    val saves: Int,
    val shares: Int,
)

@Serializable
data class PostCommunityDto(
    val id: String,
    val name: String,
    val joined: Boolean
)

