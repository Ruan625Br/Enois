package dev.jn.enois.presentation.chat.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.jn.enois.core.util.formatTime
import dev.jn.enois.domain.model.Conversation
import dev.jn.enois.domain.model.UserProfile
import dev.jn.enois.presentation.chat.thread.components.chatTitleSharedBounds
import dev.jn.enois.presentation.chat.thread.components.userAvatarSharedBounds
import dev.jn.enois.presentation.components.avatar.UserAvatar

@Composable
fun ConversationRow(
    conversation: Conversation,
    otherUser: UserProfile?,
    selecting: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val visible = otherUser != null
    val title = conversation.title ?: otherUser?.name ?: "Conversa"
    val preview =
        if (conversation.lastMessageE2e) "🔒 Mensagem cifrada" else conversation.lastMessage.orEmpty()
    val time = remember(conversation.time) { formatTime(conversation.time) }

    AnimatedVisibility(
        visible = visible,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (selecting) Checkbox(checked = selected, onCheckedChange = null)
            UserAvatar(
                modifier = Modifier
                    .userAvatarSharedBounds(),
                url = otherUser?.avatar.orEmpty(),
                userName = title,
            )
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedContent(
                        targetState = title,
                        label = "conversation-title",
                        modifier = Modifier.weight(1f)

                    ) { title ->
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .chatTitleSharedBounds(),
                        )
                    }
                    Text(
                        text = time,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        preview,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (conversation.unread > 0) Badge { Text(conversation.unread.toString()) }
                }
            }
        }
    }
}