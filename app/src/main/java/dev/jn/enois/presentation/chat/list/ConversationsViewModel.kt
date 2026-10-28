package dev.jn.enois.presentation.chat.list

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jn.enois.domain.model.Conversation
import dev.jn.enois.domain.repository.MessageRepository
import dev.jn.enois.presentation.chat.ConversationCache
import dev.jn.enois.presentation.profile.CurrentUserStore
import dev.jn.enois.presentation.profile.UserProfileCache
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
class ConversationsViewModel(
    private val repo: MessageRepository,
    private val currentUser: CurrentUserStore,
    private val profiles: UserProfileCache,
    private val conversationCache: ConversationCache,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationsUiState())
    val uiState = combine(_uiState, currentUser.profile) { state, me ->
        state.copy(userProfile = me)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _uiState.value.copy(userProfile = currentUser.profile.value),
    )
    private var job: Job? = null

    init {
        currentUser.ensureLoaded()
        loadMore()
    }

    fun selectTab(tab: InboxTab) {
        val changedSource = _uiState.value.tab.isArchived != tab.isArchived
        _uiState.update { it.copy(tab = tab, selecting = false, selected = emptySet()) }
        if (changedSource) {
            job?.cancel()
            _uiState.update {
                it.copy(
                    items = emptyList(),
                    cursor = null,
                    endReached = false,
                    isLoading = false
                )
            }
            loadMore()
        }
    }

    fun loadMore() {
        val s = _uiState.value
        if (s.isLoading || s.endReached) return
        _uiState.update { it.copy(isLoading = true) }
        job = viewModelScope.launch {
            fetch(s.tab, s.cursor)
                .onSuccess { page ->
                    _uiState.update {
                        it.copy(
                            items = it.items + page.items,
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
        val tab = _uiState.value.tab
        viewModelScope.launch {
            fetch(tab, null).onSuccess { page ->
                _uiState.update { st ->
                    if (st.tab.isArchived != tab.isArchived) return@update st
                    val fresh = page.items.map { it.id }.toSet()
                    val first = st.items.isEmpty()
                    st.copy(
                        items = page.items + st.items.filterNot { it.id in fresh },
                        cursor = if (first) page.nextCursor else st.cursor,
                        endReached = if (first) page.nextCursor == null else st.endReached,
                    )
                }
            }
        }
    }

    private suspend fun fetch(tab: InboxTab, cursor: String?) =
        if (tab.isArchived) repo.archivedConversations(cursor, PAGE_SIZE)
        else repo.listConversations(cursor, PAGE_SIZE)

    fun toggleSelected(id: String) = _uiState.update { st ->
        val next = if (id in st.selected) st.selected - id else st.selected + id
        st.copy(selecting = true, selected = next)
    }

    fun selectAll() = _uiState.update { st -> st.copy(selected = st.visible.map { it.id }.toSet()) }
    fun cancelSelection() = _uiState.update { it.copy(selecting = false, selected = emptySet()) }

    fun applyToSelected() {
        val st = _uiState.value
        val ids = st.selected.toList().ifEmpty { return }
        viewModelScope.launch {
            val result =
                if (st.tab.isArchived) repo.unhideConversations(ids) else repo.hideConversations(ids)
            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            items = it.items.filterNot { c -> c.id in ids },
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

    fun onOpened(c: Conversation) {
        conversationCache.put(c)
        _uiState.update { st -> st.copy(items = st.items.map { if (it.id == c.id) it.copy(unread = 0) else it }) }
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}