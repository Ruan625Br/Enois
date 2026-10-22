package dev.jn.enois.presentation.reactions

import androidx.compose.runtime.Stable
import dev.jn.enois.domain.model.ReactionList
import dev.jn.enois.domain.model.ReactionSummary
import dev.jn.enois.domain.model.ReactionType

@Stable
data class PostReactionsState(
    val summary: ReactionSummary? = null,
    val userReaction: ReactionType? = null,
    val fullList: ReactionList? = null,
    val isInitialized: Boolean = false,
    val isLoading: Boolean = false,
    val isPending: Boolean = false,
    val error: String? = null,
)