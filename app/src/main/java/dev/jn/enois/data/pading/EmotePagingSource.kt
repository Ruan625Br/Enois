package dev.jn.enois.data.pading

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.jn.enois.domain.model.Emote
import dev.jn.enois.domain.repository.EmoteRepository

class EmotePagingSource(
    private val repo: EmoteRepository,
    private val query: String?,
) : PagingSource<Int, Emote>() {

    override fun getRefreshKey(state: PagingState<Int, Emote>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Emote> {
        val page = params.key ?: 1
        return repo.page(page, query).fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.items,
                    prevKey = null,
                    nextKey = if (page < it.totalPages) page + 1 else null,
                )
            },
            onFailure = { LoadResult.Error(it) },
        )
    }
}