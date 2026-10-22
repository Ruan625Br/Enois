package dev.jn.enois.data.remote.dto.reaction

import dev.jn.enois.data.remote.dto.feed.PostCountersDto
import dev.jn.enois.data.remote.serialization.ReactionSummaryDtoSerializer
import dev.jn.enois.domain.model.ReactionType
import kotlinx.serialization.Serializable

@Serializable
data class ReactionListResponseDto(
    val postId: String,
    val summary: ReactionSummaryDto,
    val reactions: List<ReactionUserDto>,
    val partial: Boolean
)

@Serializable
data class ReactionUserDto(
    val userId: String,
    val type: ReactionType,
    val createdAt: String
)

@Serializable(with = ReactionSummaryDtoSerializer::class)
data class ReactionSummaryDto(
    val counts: Map<ReactionType, Int> = emptyMap(),
)

@Serializable
data class ReactionRequest(
    val type: ReactionType
)

@Serializable
data class PostActionDto(
    val postId: String,
    val status: String,
    val counters: PostCountersDto
)