package dev.jn.enois.presentation.components.comments

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jn.enois.domain.model.Comment
import dev.jn.enois.domain.model.CommentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

private const val PAGE_SIZE = 20

@Stable
class CommentsViewModel(
    private val repository: CommentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommentsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<CommentsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var loadJob: Job? = null

    fun load(postId: String) {
        if (_uiState.value.postId == postId) return
        _uiState.value = CommentsUiState(postId = postId, isLoading = true)
        startFirstPage(postId)
    }

    fun retry() {
        val postId = _uiState.value.postId ?: return
        _uiState.update { it.copy(isLoading = true, isLoadingMore = false, error = null) }
        startFirstPage(postId)
    }

    private fun startFirstPage(postId: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getComments(postId, cursor = null, limit = PAGE_SIZE)
                .onSuccess { page ->
                    updateFor(postId) {
                        it.copy(
                            comments = page.comments,
                            nextCursor = page.nextCursor,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
                .onFailure { error ->
                    error.rethrowIfCancelled()
                    Log.e(TAG, "load", error)
                    updateFor(postId) {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Não foi possível carregar os comentários."
                        )
                    }
                }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        val postId = state.postId ?: return
        val cursor = state.nextCursor ?: return
        if (state.isLoading || state.isLoadingMore) return

        updateFor(postId) { it.copy(isLoadingMore = true, error = null) }

        loadJob = viewModelScope.launch {
            repository.getComments(postId, cursor, PAGE_SIZE)
                .onSuccess { page ->
                    updateFor(postId) {
                        it.copy(
                            comments = (it.comments + page.comments).distinctBy(Comment::id),
                            nextCursor = page.nextCursor,
                            isLoadingMore = false,
                        )
                    }
                }
                .onFailure { error ->
                    error.rethrowIfCancelled()
                    Log.e(TAG, "loadMore", error)
                    updateFor(postId) {
                        it.copy(
                            isLoadingMore = false,
                            error = error.message ?: "Não foi possível carregar mais."
                        )
                    }
                }
        }
    }

    fun onReply(comment: Comment) = _uiState.update { it.copy(replyingTo = comment) }

    fun cancelReply() = _uiState.update { it.copy(replyingTo = null) }

    fun dismissSendError() = _uiState.update { it.copy(sendError = null) }

    fun send(content: String) {
        val state = _uiState.value
        val postId = state.postId ?: return
        val text = content.trim()
        if (text.isEmpty() || state.isSending) return

        val parentId = state.replyingTo?.id
        updateFor(postId) { it.copy(isSending = true, sendError = null) }

        viewModelScope.launch {
            val replyingTo = state.replyingTo
            val mentions = listOfNotNull(
                replyingTo
                    ?.takeIf { it.authorUsername != null && text.contains("@${it.authorUsername}") }
                    ?.authorId
            )
            repository.createComment(postId, text, parentId, mentions)
                .onSuccess { created ->
                    updateFor(postId) {
                        it.copy(
                            comments = listOf(created) + it.comments.filterNot { c -> c.id == created.id },
                            isSending = false,
                            replyingTo = null,
                        )
                    }
                    _events.send(CommentsEvent.Sent(postId))
                }
                .onFailure { error ->
                    error.rethrowIfCancelled()
                    Log.e(TAG, "send", error)
                    updateFor(postId) {
                        it.copy(
                            isSending = false,
                            sendError = error.message ?: "Não foi possível enviar o comentário."
                        )
                    }
                }
        }
    }

    private fun updateFor(postId: String, transform: (CommentsUiState) -> CommentsUiState) {
        _uiState.update { if (it.postId == postId) transform(it) else it }
    }

    private fun Throwable.rethrowIfCancelled() {
        if (this is CancellationException) throw this
    }

    private companion object {
        const val TAG = "CommentsViewModel"
    }
}