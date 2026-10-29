package dev.jn.enois.presentation.chat.thread

import android.os.SystemClock
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jn.enois.domain.model.Message
import dev.jn.enois.domain.model.MessageStatus
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.domain.model.UserProfile
import dev.jn.enois.domain.repository.MessageRepository
import dev.jn.enois.domain.util.withReaction
import dev.jn.enois.presentation.chat.ConversationCache
import dev.jn.enois.presentation.profile.CurrentUserStore
import dev.jn.enois.presentation.profile.ProfileState
import dev.jn.enois.presentation.profile.UserProfileCache
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

private data class ReactionOverride(
    val reactions: Map<ReactionType, Int>,
    val myReaction: ReactionType?,
    val expiresAt: Long,
)


@Stable
class ChatViewModel(
    private val conversationId: String,
    private val messageRepository: MessageRepository,
    private val conversationCache: ConversationCache,
    private val profiles: UserProfileCache,
    private val currentUser: CurrentUserStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    private val _conversation = MutableStateFlow(conversationCache.get(conversationId))
    val conversation = _conversation.asStateFlow()
    val currentUserId = currentUser.userId

    @OptIn(ExperimentalCoroutinesApi::class)
    val peer: StateFlow<UserProfile?> = combine(_conversation, currentUserId) { c, me ->
        c?.participantIds?.firstOrNull { it != me }
    }
        .distinctUntilChanged()
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else {
                profiles.ensure(id)
                profiles.observe(id).map { (it as? ProfileState.Loaded)?.profile }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val idempotencyKeys = mutableMapOf<String, String>()

    private val reactionOverrides = mutableMapOf<String, ReactionOverride>()

    private fun reconcile(server: Message, now: Long): Message {
        val o = reactionOverrides[server.id] ?: return server
        return when {
            server.myReaction == o.myReaction -> {
                reactionOverrides.remove(server.id); server
            }

            now < o.expiresAt -> server.copy(reactions = o.reactions, myReaction = o.myReaction)
            else -> {
                reactionOverrides.remove(server.id); server
            }
        }
    }

    init {
        currentUser.ensureLoaded()
        refreshConversation()
        loadMore()
        viewModelScope.launch { messageRepository.markAsRead(conversationId) }
    }

    fun loadMore() {
        val s = _uiState.value
        if (s.isLoading || s.endReached) return
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            messageRepository.listMessages(conversationId, s.cursor)
                .onSuccess { page ->
                    _uiState.update {
                        it.copy(
                            messages = it.messages + page.items,
                            cursor = page.nextCursor,
                            endReached = page.nextCursor == null,
                            isLoading = false,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            messageRepository.listMessages(conversationId, null).onSuccess { page ->
                val now = SystemClock.elapsedRealtime()
                val fresh = page.items.map { reconcile(it, now) }

                _uiState.update { st ->
                    val freshIds = fresh.mapTo(HashSet()) { it.id }
                    val pending = st.messages.filter { it.status != MessageStatus.Sent }
                    val older =
                        st.messages.filter { it.status == MessageStatus.Sent && it.id !in freshIds }
                    st.copy(messages = pending + fresh + older)
                }
            }
            refreshConversation()
        }
    }

    private fun refreshConversation() {
        viewModelScope.launch {
            messageRepository.getConversation(conversationId).onSuccess { c ->
                _conversation.value = c
                _uiState.update { it.copy(peerLastReadMillis = c.otherLastReadMillis) }
            }
        }
    }

    fun send(text: String) {
        val content = text.trim().ifEmpty { return }
        val me = currentUserId.value ?: return
        val tempId = "temp-${UUID.randomUUID()}"
        idempotencyKeys[tempId] = UUID.randomUUID().toString()
        _uiState.update {
            it.copy(
                messages = listOf(
                    Message.pending(
                        tempId,
                        conversationId,
                        me,
                        content
                    )
                ) + it.messages
            )
        }
        dispatch(tempId, content)
    }

    fun retry(tempId: String) {
        val msg = _uiState.value.messages.firstOrNull { it.id == tempId } ?: return
        setStatus(tempId, MessageStatus.Sending)
        dispatch(tempId, msg.text)
    }

    private fun dispatch(tempId: String, content: String) {
        viewModelScope.launch {
            messageRepository.sendMessage(
                conversationId,
                content,
                emptyList(),
                false,
                idempotencyKeys.getValue(tempId)
            )
                .onSuccess { sent ->
                    idempotencyKeys.remove(tempId)
                    _uiState.update { st ->
                        st.copy(messages = st.messages.filterNot { it.id == sent.id }
                            .map { if (it.id == tempId) sent else it })
                    }
                }
                .onFailure { setStatus(tempId, MessageStatus.Failed) }
        }
    }

    private fun setStatus(id: String, status: MessageStatus) = _uiState.update { st ->
        st.copy(messages = st.messages.map { if (it.id == id) it.copy(status = status) else it })
    }

    fun react(messageId: String, type: ReactionType) {
        val state = _uiState.value
        val message = state.messages.firstOrNull { it.id == messageId } ?: return
        if (message.status != MessageStatus.Sent || messageId in state.reactionPendingIds) return

        val previous = message.myReaction
        val next = if (previous == type) null else type
        val optimistic = message.reactions.withReaction(previous, next)

        reactionOverrides[messageId] =
            ReactionOverride(optimistic, next, expiresAt = Long.MAX_VALUE)

        _uiState.update { st ->
            st.copy(
                reactionPendingIds = st.reactionPendingIds + messageId,
                messages = st.messages.map {
                    if (it.id == messageId) it.copy(
                        reactions = optimistic,
                        myReaction = next
                    ) else it
                },
            )
        }

        viewModelScope.launch {
            val result = messageRepository.react(conversationId, messageId, type)
            _uiState.update { it.copy(reactionPendingIds = it.reactionPendingIds - messageId) }

            result
                .onSuccess { status ->
                    if (status == "REACTED") {
                        reactionOverrides[messageId]?.let {
                            reactionOverrides[messageId] =
                                it.copy(expiresAt = SystemClock.elapsedRealtime() + OVERRIDE_GRACE_MS)
                        }
                    } else {
                        reactionOverrides.remove(messageId)
                        refresh()
                    }
                }
                .onFailure { e ->
                    reactionOverrides.remove(messageId)
                    _uiState.update { st ->
                        st.copy(
                            error = e.message,
                            messages = st.messages.map {
                                if (it.id == messageId) it.copy(
                                    reactions = message.reactions,
                                    myReaction = previous
                                ) else it
                            },
                        )
                    }
                }
        }
    }


    fun startSelecting() = _uiState.update { it.copy(selecting = true) }
    fun cancelSelection() = _uiState.update { it.copy(selecting = false, selected = emptySet()) }
    fun toggleSelected(id: String) = _uiState.update { st ->
        st.copy(selected = if (id in st.selected) st.selected - id else st.selected + id)
    }


    fun hideSelected() {
        val ids = _uiState.value.selected.toList().ifEmpty { return }
        viewModelScope.launch {
            messageRepository.hideMessages(conversationId, ids)
                .onSuccess {
                    _uiState.update { st ->
                        st.copy(
                            messages = st.messages.filterNot { it.id in ids },
                            selecting = false,
                            selected = emptySet()
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun ensureProfile(id: String) = profiles.ensure(id)
    fun profileState(id: String) = profiles.observe(id)

    private companion object {
        const val OVERRIDE_GRACE_MS = 10_000L
    }
}