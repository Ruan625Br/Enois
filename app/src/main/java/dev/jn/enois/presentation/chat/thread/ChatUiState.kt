package dev.jn.enois.presentation.chat.thread

import androidx.compose.runtime.Stable
import dev.jn.enois.domain.model.Message

@Stable
data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val reactionPendingIds: Set<String> = emptySet(),
    val cursor: String? = null,
    val endReached: Boolean = false,
    val isLoading: Boolean = false,
    val peerLastReadMillis: Long? = null,
    val selecting: Boolean = false,
    val selected: Set<String> = emptySet(),
    val error: String? = null,
)