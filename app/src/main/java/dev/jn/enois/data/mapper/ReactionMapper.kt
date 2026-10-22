package dev.jn.enois.data.mapper

import dev.jn.enois.data.remote.dto.reaction.PostActionDto
import dev.jn.enois.data.remote.dto.reaction.ReactionListResponseDto
import dev.jn.enois.data.remote.dto.reaction.ReactionSummaryDto
import dev.jn.enois.data.remote.dto.reaction.ReactionUserDto
import dev.jn.enois.domain.model.PostAction
import dev.jn.enois.domain.model.ReactionList
import dev.jn.enois.domain.model.ReactionSummary
import dev.jn.enois.domain.model.ReactionUser

fun ReactionListResponseDto.toDomain(): ReactionList = ReactionList(
    postId = postId,
    summary = summary.toDomain(),
    reactions = reactions.map { it.toDomain() },
    partial = partial
)

fun ReactionUserDto.toDomain(): ReactionUser = ReactionUser(
    userId = userId,
    type = type,
    createdAt = createdAt
)

fun ReactionSummaryDto.toDomain(): ReactionSummary = ReactionSummary(
    counts = counts
)

fun PostActionDto.toDomain(): PostAction = PostAction(
    postId = postId,
    status = status,
    counters = counters.toDomain()
)