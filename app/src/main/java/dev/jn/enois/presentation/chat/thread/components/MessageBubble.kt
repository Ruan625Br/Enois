package dev.jn.enois.presentation.chat.thread.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.jn.enois.core.util.formatTime
import dev.jn.enois.domain.model.Message
import dev.jn.enois.domain.model.MessageStatus
import dev.jn.enois.domain.model.UserProfile
import dev.jn.enois.presentation.components.avatar.UserAvatar

@Composable
fun MessageBubble(
    message: Message,
    senderProfile: UserProfile?,
    isMine: Boolean,
    readByPeer: Boolean,
    selecting: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRetry: () -> Unit,
) {
    val body = if (message.isE2e) "🔒 Mensagem cifrada" else message.text

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            AnimatedVisibility(
                visible = !isMine && senderProfile != null
            ) {
                if (senderProfile != null) {
                    UserAvatar(
                        url = senderProfile.avatar.orEmpty(),
                        userName = senderProfile.name,
                        size = 30.dp,
                        shape = CircleShape,
                        textStyle = MaterialTheme.typography.labelMedium,
                    )

                }

            }
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp, topEnd = 18.dp,
                    bottomStart = if (isMine) 18.dp else 4.dp,
                    bottomEnd = if (isMine) 4.dp else 18.dp,
                ),
                color = if (isMine) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .combinedClickable(onClick = onClick, onLongClick = onLongClick),
            ) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    if (body.isNotEmpty()) {
                        Text(body, style = MaterialTheme.typography.bodyLarge)
                    }

                    Row(
                        modifier = Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                     /*   Text(
                            text = formatTime(message.time),
                            style = MaterialTheme.typography.labelSmall,
                        )*/

                        if (isMine) when (message.status) {
                            MessageStatus.Sending -> Text(
                                "…",
                                style = MaterialTheme.typography.labelSmall
                            )

                            MessageStatus.Sent -> Text(
                                if (readByPeer) "✓✓" else "✓",
                                style = MaterialTheme.typography.labelSmall
                            )

                            MessageStatus.Failed -> Text(
                                "Falhou, tentar de novo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.clickable(onClick = onRetry),
                            )
                        }
                    }
                }
            }
        }
    }
}