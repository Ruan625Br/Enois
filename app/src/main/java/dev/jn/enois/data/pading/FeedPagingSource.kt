package dev.jn.enois.data.pading

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.domain.model.FeedTab
import dev.jn.enois.domain.repository.FeedRepository
import kotlin.coroutines.cancellation.CancellationException

class FeedPagingSource(
    private val repository: FeedRepository,
    private val tab: FeedTab
) : PagingSource<String, FeedPost>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, FeedPost> {
        return try {
            val result = repository.getFeed(
                tab,
                cursor = params.key,
                limit = params.loadSize.coerceAtMost(50)
            )

            result.fold(
                onSuccess = { page ->
                    LoadResult.Page(
                        data = page.posts,
                        prevKey = null,
                        nextKey = page.nextCursor
                    )
                },
                onFailure = { error ->
                    LoadResult.Error(error)
                }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, FeedPost>): String? = null
}