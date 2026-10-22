package dev.jn.enois.domain.repository

import dev.jn.enois.domain.model.PostAction
import dev.jn.enois.domain.model.ReactionList
import dev.jn.enois.domain.model.ReactionType

interface ReactionRepository {

    suspend fun addReaction(postId: String, type: ReactionType): Result<PostAction>

    suspend fun removeReaction(postId: String): Result<PostAction>

    suspend fun getReactions(postId: String): Result<ReactionList>
}