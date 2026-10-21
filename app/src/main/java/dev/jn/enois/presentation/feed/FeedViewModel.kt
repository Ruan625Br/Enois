package dev.jn.enois.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.jn.enois.data.pading.FeedPagingSource
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.domain.model.FeedTab
import dev.jn.enois.domain.repository.FeedRepository
import dev.jn.enois.domain.repository.UserProfileRepository
import dev.jn.enois.presentation.postdetails.PostDetailsCache
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedRepository: FeedRepository,
    private val userProfileRepository: UserProfileRepository,
    private val postDetailsCache: PostDetailsCache
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

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
        loadCurrentUserProfile()
    }

    fun onSelectTab(tab: FeedTab) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectedTab = tab)
            }
        }
    }

    private fun loadCurrentUserProfile() = viewModelScope.launch {
        val result = userProfileRepository.getCurrentProfile()

        result.onSuccess { userProfile ->
            _uiState.update {
                it.copy(userProfile = userProfile)
            }
        }
    }

    fun addPostToCache(post: FeedPost) {
        postDetailsCache.put(post)
    }

}