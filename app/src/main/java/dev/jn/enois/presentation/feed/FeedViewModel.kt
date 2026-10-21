package dev.jn.enois.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.jn.enois.data.pading.FeedPagingSource
import dev.jn.enois.domain.model.FeedTab
import dev.jn.enois.domain.repository.FeedRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedRepository: FeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts = _uiState.flatMapLatest { state ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FeedPagingSource(feedRepository, state.selectedTab)
            }
        ).flow
    }
        .cachedIn(viewModelScope)


    fun onSelectTab(tab: FeedTab) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectedTab = tab)
            }
        }
    }

}