package dev.jn.enois.presentation.chat.thread

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.domain.model.UserProfile
import dev.jn.enois.presentation.chat.thread.components.ChatInput
import dev.jn.enois.presentation.chat.thread.components.ChatToolbar
import dev.jn.enois.presentation.chat.thread.components.MessageList
import dev.jn.enois.presentation.profile.ProfileState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ChatRoute(
    conversationId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel(parameters = { parametersOf(conversationId) }),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val conversation by viewModel.conversation.collectAsStateWithLifecycle()
    val peer by viewModel.peer.collectAsStateWithLifecycle()
    val me by viewModel.currentUserId.collectAsStateWithLifecycle()

    val owner = LocalLifecycleOwner.current
    LaunchedEffect(owner) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                delay(5_000.milliseconds); viewModel.refresh()
            }
        }
    }

    ChatScreen(
        uiState = uiState,
        title = conversation?.title ?: peer?.name ?: "Conversa",
        peer = peer,
        currentUserId = me,
        onBack = onBack,
        onSend = viewModel::send,
        onRetry = viewModel::retry,
        onLoadOlder = viewModel::loadMore,
        onReact = viewModel::react,
        onStartSelecting = viewModel::startSelecting,
        onToggleSelected = viewModel::toggleSelected,
        onCancelSelection = viewModel::cancelSelection,
        onDeleteSelected = viewModel::hideSelected,
        profileState = viewModel::profileState,
        ensureProfile = viewModel::ensureProfile,
        modifier = modifier,
    )
}

@Composable
private fun ChatScreen(
    uiState: ChatUiState,
    title: String,
    peer: UserProfile?,
    currentUserId: String?,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    onRetry: (String) -> Unit,
    onLoadOlder: () -> Unit,
    onReact: (String, ReactionType) -> Unit,
    onStartSelecting: () -> Unit,
    onToggleSelected: (String) -> Unit,
    onCancelSelection: () -> Unit,
    onDeleteSelected: () -> Unit,
    profileState: (String) -> Flow<ProfileState?>,
    ensureProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            ChatToolbar(
                title = title,
                profile = peer,
                onBack = onBack,
            )
        },
        bottomBar = {
            if (!uiState.selecting) {
                ChatInput(
                    onSend = onSend,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        },
    ) { innerPadding ->
        MessageList(
            messages = uiState.messages,
            currentUserId = currentUserId,
            peerLastReadMillis = uiState.peerLastReadMillis,
            selecting = uiState.selecting,
            reactionPendingIds = uiState.reactionPendingIds,
            selected = uiState.selected,
            isLoading = uiState.isLoading,
            onLoadOlder = onLoadOlder,
            onToggleSelected = onToggleSelected,
            onRetry = onRetry,
            onReact = onReact,
            profileState = profileState,
            ensureProfile = ensureProfile,
            contentPadding = innerPadding,
        )
    }
}