package dev.jn.enois.data.repository

import dev.jn.enois.data.local.session.TokenStorage
import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.ReactionApi
import dev.jn.enois.data.remote.dto.reaction.ReactionRequest
import dev.jn.enois.domain.model.PostAction
import dev.jn.enois.domain.model.ReactionList
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.domain.repository.ReactionRepository
import kotlin.coroutines.cancellation.CancellationException

class ReactionRepositoryImpl(
    private val reactionApi: ReactionApi,
    private val tokenStorage: TokenStorage
) : ReactionRepository {

    override suspend fun addReaction(
        postId: String,
        type: ReactionType
    ): Result<PostAction> {
        return try {
            val tokens = tokenStorage.getTokens()
                ?: error("No authenticated session")

            val request = ReactionRequest(type)
            val response = reactionApi.react(postId, request, tokens.accessToken)

            Result.success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeReaction(postId: String): Result<PostAction> {
        return try {
            val tokens = tokenStorage.getTokens()
                ?: error("No authenticated session")

            val response = reactionApi.removeReaction(postId, tokens.accessToken)

            Result.success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReactions(postId: String): Result<ReactionList> {
        return try {
            val tokens = tokenStorage.getTokens()
                ?: error("No authenticated session")

            val response = reactionApi.getReactions(postId, tokens.accessToken)

            Result.success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}