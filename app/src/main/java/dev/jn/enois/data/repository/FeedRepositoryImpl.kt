package dev.jn.enois.data.repository

import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.FeedApi
import dev.jn.enois.domain.model.FeedPage
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.domain.model.FeedTab
import dev.jn.enois.domain.repository.FeedRepository
import kotlin.coroutines.cancellation.CancellationException

class FeedRepositoryImpl(
    private val feedApi: FeedApi,
) : FeedRepository {

    override suspend fun getFeed(
        tab: FeedTab,
        cursor: String?,
        limit: Int
    ): Result<FeedPage> {
        return try {
            val response = feedApi.getFeed(
                tab = tab.name,
                cursor = cursor,
                limit = limit
            )

            Result.success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPost(postId: String): Result<FeedPost> {
        return try {
            val response = feedApi.getPost(
                postId = postId
            )

            Result.success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}