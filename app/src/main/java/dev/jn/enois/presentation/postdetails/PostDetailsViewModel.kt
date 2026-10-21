package dev.jn.enois.presentation.postdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jn.enois.domain.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostDetailsViewModel(
    private val feedRepository: FeedRepository,
    private val postId: String,
    private val postDetailsCache: PostDetailsCache
) : ViewModel() {
    val initialPost = postDetailsCache.get(postId)

    private val _uiState = MutableStateFlow(PostDetailsUiState(initialPost))
    val uiState = _uiState.asStateFlow()

    init {
        loadPost()
    }

    private fun loadPost() {
        viewModelScope.launch {
            val result = feedRepository.getPost(postId)
            result.onSuccess { post ->
                _uiState.update {
                    it.copy(post = post)
                }
            }.onFailure { error ->
                Log.e("PostDetailsViewModel", "Error loading post", error)
            }
        }
    }
}