package dev.jn.enois.presentation.chat.thread.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.Message
import dev.jn.enois.presentation.profile.ProfileState
import kotlinx.coroutines.flow.Flow

@Composable
fun MessageList(
    messages: List<Message>,
    currentUserId: String?,
    peerLastReadMillis: Long?,
    selecting: Boolean,
    selected: Set<String>,
    isLoading: Boolean,
    onLoadOlder: () -> Unit,
    onToggleSelected: (String) -> Unit,
    onRetry: (String) -> Unit,
    profileState: (String) -> Flow<ProfileState?>,
    ensureProfile: (String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding + PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(messages, key = { it.id }) { m ->
            val isMine = m.senderId == currentUserId

            LaunchedEffect(m.senderId) {
                ensureProfile(m.senderId)
            }
            val senderProfileState by remember(m.senderId) {
                profileState(m.senderId)
            }.collectAsState(initial = null)

            val senderProfile = (senderProfileState as? ProfileState.Loaded)?.profile

            MessageBubble(
                message = m,
                senderProfile = senderProfile,
                isMine = isMine,
                readByPeer = isMine && peerLastReadMillis != null && m.timeMillis <= peerLastReadMillis,
                selecting = selecting,
                selected = m.id in selected,
                onClick = { if (selecting) onToggleSelected(m.id) },
                onLongClick = { onToggleSelected(m.id) },
                onRetry = { onRetry(m.id) },

                )
        }
        if (isLoading) item(key = "loading") {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                Alignment.Center
            ) { CircularProgressIndicator() }
        }
    }

    val nearEnd by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= listState.layoutInfo.totalItemsCount - 5
        }
    }
    LaunchedEffect(nearEnd, messages.size) { if (nearEnd) onLoadOlder() }

    val newestId = messages.firstOrNull()?.id
    LaunchedEffect(newestId) {
        if (listState.firstVisibleItemIndex <= 1) listState.animateScrollToItem(0)
    }
}