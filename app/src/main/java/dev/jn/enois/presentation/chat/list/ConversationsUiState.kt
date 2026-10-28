package dev.jn.enois.presentation.chat.list

import androidx.compose.runtime.Stable
import dev.jn.enois.domain.model.Conversation
import dev.jn.enois.domain.model.ConversationKind
import dev.jn.enois.domain.model.UserProfile


enum class InboxTab(val isArchived: Boolean = false) {
    ALL, JOBS, SERVICES, PEOPLE, ARCHIVED(true);

    fun accepts(kind: ConversationKind) = when (this) {
        ALL, ARCHIVED -> true
        JOBS -> kind == ConversationKind.JOB
        SERVICES -> kind == ConversationKind.SERVICE
        PEOPLE -> kind != ConversationKind.JOB && kind != ConversationKind.SERVICE
    }
}

@Stable
data class ConversationsUiState(
    val userProfile: UserProfile? = null,
    val tab: InboxTab = InboxTab.ALL,
    val items: List<Conversation> = emptyList(),
    val cursor: String? = null,
    val endReached: Boolean = false,
    val isLoading: Boolean = false,
    val selecting: Boolean = false,
    val selected: Set<String> = emptySet(),
    val error: String? = null,
) {
    val visible get() = items.filter { tab.accepts(it.kind) }
}
