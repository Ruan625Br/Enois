package dev.jn.enois.data.repository

import dev.jn.enois.data.local.session.TokenStorage
import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.FeedApi
import dev.jn.enois.domain.model.FeedPage
import dev.jn.enois.domain.model.FeedTab
import dev.jn.enois.domain.model.SessionState
import dev.jn.enois.domain.repository.FeedRepository
import dev.jn.enois.domain.repository.SessionRepository
import kotlin.coroutines.cancellation.CancellationException

class FeedRepositoryImpl(
    private val feedApi: FeedApi,
    private val tokenStorage: TokenStorage
) : FeedRepository {

    override suspend fun getFeed(
        tab: FeedTab,
        cursor: String?,
        limit: Int
    ): Result<FeedPage> {
        return try {
            val tokens = tokenStorage.getTokens()
                ?: error("No authenticated session")

            val response = feedApi.getFeed(
                token = tokens.accessToken,
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
}