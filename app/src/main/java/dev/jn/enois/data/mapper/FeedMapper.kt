package dev.jn.enois.data.mapper

import dev.jn.enois.data.remote.dto.feed.FeedPostDto
import dev.jn.enois.data.remote.dto.feed.FeedResponseDto
import dev.jn.enois.data.remote.dto.feed.PostCommunityDto
import dev.jn.enois.data.remote.dto.feed.PostCountersDto
import dev.jn.enois.domain.model.FeedPage
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.domain.model.PostCommunity
import dev.jn.enois.domain.model.PostCounters

fun FeedResponseDto.toDomain(): FeedPage {
    return FeedPage(
        posts = items.map { it.toDomain() },
        nextCursor = nextCursor
    )
}

fun FeedPostDto.toDomain() = FeedPost(
    id = id,
    authorId = authorId,
    authorUsername = authorUsername,
    authorName = authorName,
    authorBadge = authorBadge,
    authorBadges = authorBadges,
    authorAvatar = authorAvatar,
    authorAvatarFrame = authorAvatarFrame,
    authorIdentity = authorIdentity,
    type = type,
    content = content,
    pageHtml = pageHtml,
    articleBody = articleBody,
    tags = tags,
    mediaIds = mediaIds,
    createdAt = createdAt,
    visibility = visibility,
    counters = counters.toDomain(),
    status = status,
    currentUserReaction = currentUserReaction,
    communityId = communityId,
    community = community?.toDomain(),
    editedAt = editedAt,
    expiredAt = expiredAt,
)

fun PostCountersDto.toDomain() = PostCounters(
    reactions = reactions,
    comments = comments,
    saves = saves,
    shares = shares
)

fun PostCommunityDto.toDomain() = PostCommunity(
    id = id,
    name = name,
    joined = joined
)