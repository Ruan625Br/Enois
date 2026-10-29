package dev.jn.enois.presentation.feed

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.jn.enois.data.pading.FeedPagingSource
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.domain.model.FeedTab
import dev.jn.enois.domain.repository.FeedRepository
import dev.jn.enois.presentation.postdetails.PostDetailsCache
import dev.jn.enois.presentation.profile.CurrentUserStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
class FeedViewModel(
    private val feedRepository: FeedRepository,
    private val currentUser: CurrentUserStore,
    private val postDetailsCache: PostDetailsCache
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = combine(_uiState, currentUser.profile) { state, me ->
        state.copy(userProfile = me)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _uiState.value.copy(userProfile = currentUser.profile.value),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts = _uiState
        .map { it.selectedTab }
        .distinctUntilChanged()
        .flatMapLatest { selectedTab ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    initialLoadSize = 20,
                    prefetchDistance = 8,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    FeedPagingSource(feedRepository, selectedTab)
                }
            ).flow
        }
        .cachedIn(viewModelScope)

    init {
        currentUser.ensureLoaded()
    }

    fun onSelectTab(tab: FeedTab) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectedTab = tab)
            }
        }
    }

    fun addPostToCache(post: FeedPost) {
        postDetailsCache.put(post)
    }

}