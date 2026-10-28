package dev.jn.enois.data.repository

import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.ReactionApi
import dev.jn.enois.data.remote.dto.reaction.ReactionRequest
import dev.jn.enois.domain.model.PostAction
import dev.jn.enois.domain.model.ReactionList
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.domain.repository.ReactionRepository
import kotlin.coroutines.cancellation.CancellationException

class ReactionRepositoryImpl(
    private val reactionApi: ReactionApi
) : ReactionRepository {

    override suspend fun addReaction(
        postId: String,
        type: ReactionType
    ): Result<PostAction> {
        return try {

            val request = ReactionRequest(type)
            val response = reactionApi.react(postId, request)

            Result.success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeReaction(postId: String): Result<PostAction> {
        return try {
            val response = reactionApi.removeReaction(postId)

            Result.success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReactions(postId: String): Result<ReactionList> {
        return try {
            val response = reactionApi.getReactions(postId)

            Result.success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}