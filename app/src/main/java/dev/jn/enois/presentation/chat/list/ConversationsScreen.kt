package dev.jn.enois.presentation.chat.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import dev.jn.enois.domain.model.Conversation
import dev.jn.enois.presentation.chat.components.ConversationRowItem
import dev.jn.enois.presentation.chat.components.ConversationsToolbar
import dev.jn.enois.presentation.profile.ProfileState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ConversationsRoute(
    onBack: () -> Unit,
    openConversation: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val owner = LocalLifecycleOwner.current

    LaunchedEffect(owner) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                delay(5_000.milliseconds)
                viewModel.refresh()
            }
        }
    }


    ConversationsScreen(
        uiState = uiState,
        onBack = onBack,
        profileState = viewModel::profileState,
        ensureProfile = viewModel::ensureProfile,
        onOpenConversation = openConversation,
        onToggleSelected = viewModel::toggleSelected,
        onOpened = viewModel::onOpened,
        modifier = modifier
    )

}

@Composable
fun ConversationsScreen(
    uiState: ConversationsUiState,
    onBack: () -> Unit,
    profileState: (String) -> Flow<ProfileState?>,
    ensureProfile: (String) -> Unit,
    onOpenConversation: (String) -> Unit,
    onToggleSelected: (String) -> Unit,
    onOpened: (Conversation) -> Unit,
    modifier: Modifier = Modifier
) {


    Scaffold(
        modifier = modifier,
        topBar = {
            ConversationsToolbar(
                onBack = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = modifier
        ) {
            items(uiState.visible, key = { it.id }) { conversation ->
                ConversationRowItem(
                    conversation = conversation,
                    currentUserId = uiState.userProfile?.id,
                    profileState = profileState,
                    ensureProfile = ensureProfile,
                    selecting = uiState.selecting,
                    selected = conversation.id in uiState.selected,
                    onClick = {
                        if (uiState.selecting) {
                            onToggleSelected(conversation.id)
                        } else {
                            onOpened(conversation)
                            onOpenConversation(conversation.id)
                        }
                    },
                    onLongClick = {
                        onToggleSelected(conversation.id)
                    }
                )
            }
        }
    }
}