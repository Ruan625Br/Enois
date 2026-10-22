package dev.jn.enois.presentation.reactions

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jn.enois.domain.model.ReactionSummary
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.domain.repository.ReactionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
class ReactionViewModel(
    val repository: ReactionRepository
) : ViewModel() {

    private val _states =
        MutableStateFlow<Map<String, PostReactionsState>>(emptyMap())

    val states = _states.asStateFlow()
    private val loadingJobs = mutableMapOf<String, Job>()

    fun provideFeedSummary(
        postId: String,
        summary: ReactionSummary?,
        currentUserReaction: ReactionType?,
    ) {
        _states.update { states ->
            val previous = states[postId] ?: PostReactionsState()

            if (previous.isInitialized) {
                states
            } else {
                states + (
                        postId to previous.copy(
                            summary = summary,
                            userReaction = currentUserReaction,
                            isInitialized = true
                        ))

            }
        }
    }

    fun loadFullList(postId: String) {
        val current = _states.value[postId] ?: PostReactionsState()

        if (current.isLoading) return

        if (current.fullList != null) return

        _states.update { states ->
            val previous = states[postId] ?: PostReactionsState()

            states + (postId to previous.copy(
                isLoading = true,
                error = null
            ))
        }

        loadingJobs[postId] = viewModelScope.launch {
            repository.getReactions(postId)
                .onSuccess { reactionList ->
                    _states.update { states ->
                        val previous = states[postId] ?: PostReactionsState()

                        states + (postId to previous.copy(
                            summary = reactionList.summary,
                            fullList = reactionList,
                            isLoading = false,
                            error = null
                        ))

                    }
                }
                .onFailure { error ->
                    Log.e("ReactionViewModel", "loadFullList", error)
                    _states.update { states ->
                        val previous =
                            states[postId] ?: PostReactionsState()

                        states + (
                                postId to previous.copy(
                                    isLoading = false,
                                    error = error.message
                                        ?: "Não foi possível carregar as reações.",
                                )
                                )
                    }
                }
            loadingJobs.remove(postId)

        }
    }

    fun onReactionAction(action: ReactionAction) = viewModelScope.launch {
        when (action) {
            is ReactionAction.Click -> onReactionClick(action)
        }
    }

    ///togle
    private suspend fun onReactionClick(action: ReactionAction.Click) {
        val postId = action.postId
        val current = _states.value[action.postId] ?: PostReactionsState()

        if (current.isPending) return

        val previousReaction = current.userReaction
        val remove = previousReaction == action.type

        val newReaction = if (remove) null else action.type

        val optimisticSummary = current.summary?.let { summary ->
            val counts = summary.counts.toMutableMap()

            if (previousReaction != null) {
                val remaining = (counts[previousReaction] ?: 0) - 1

                if (remaining > 0) {
                    counts[previousReaction] = remaining
                } else {
                    counts.remove(previousReaction)
                }
            }

            if (newReaction != null) {
                counts[newReaction] = (counts[newReaction] ?: 0) + 1
            }

            ReactionSummary(counts = counts)
        }

        _states.update { states ->
            states + (
                    postId to current.copy(
                        summary = optimisticSummary,
                        userReaction = newReaction,
                        isPending = true,
                        error = null,
                    )
                    )
        }

        val result = if (remove) {
            repository.removeReaction(postId)
        } else {
            repository.addReaction(postId, action.type)
        }


        result
            .onSuccess {
                _states.update { states ->
                    val latest = states[postId] ?: current

                    states + (
                            postId to latest.copy(
                                isPending = false,
                                error = null,
                            )
                            )
                }
            }
            .onFailure { error ->
                _states.update { states ->
                    states + (
                            postId to current.copy(
                                isPending = false,
                                error = error.message
                                    ?: "Não foi possível atualizar a reação.",
                            )
                            )
                }
            }
    }
}