package dev.jn.enois.presentation.chat.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.jn.enois.domain.model.Conversation
import dev.jn.enois.presentation.profile.ProfileState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ConversationRowItem(
    conversation: Conversation,
    currentUserId: String?,
    profileState: (String) -> Flow<ProfileState?>,
    ensureProfile: (String) -> Unit,
    selecting: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val otherId = remember(conversation.participantIds, currentUserId) {
        conversation.participantIds.firstOrNull { it != currentUserId }
    }
    LaunchedEffect(otherId) { otherId?.let(ensureProfile) }

    val state by remember(otherId) {
        otherId?.let(profileState) ?: flowOf(null)
    }.collectAsState(initial = null)
    val other = (state as? ProfileState.Loaded)?.profile

    ConversationRow(
        conversation = conversation,
        otherUser = other,
        selecting = selecting,
        selected = selected,
        onClick = onClick,
        onLongClick = onLongClick,
    )
}