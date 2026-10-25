package dev.jn.enois.presentation.components.comments

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.jn.enois.core.util.formatTime
import dev.jn.enois.domain.model.Comment
import dev.jn.enois.presentation.components.avatar.UserAvatar
import dev.jn.enois.presentation.components.badge.UserBadges

@Composable
fun CommentItem(
    comment: Comment,
    onReply: (Comment) -> Unit,
    modifier: Modifier = Modifier,
    replyingToName: String? = null,
) {
    val time = remember(comment.createdAt) { formatTime(comment.createdAt) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        UserAvatar(
            url = comment.authorAvatar.orEmpty(),
            userName = comment.authorName,
            frame = comment.authorAvatarFrame,
        )

        Column(Modifier.weight(1f)) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
            ) {
                Column(
                    Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = comment.authorName,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        Text(
                            text = "· $time",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    if (comment.authorBadges.isNotEmpty()) UserBadges(badges = comment.authorBadges)

                    if (comment.pinned) {
                        Text(
                            text = "📌 Fixado",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    if (replyingToName != null) {
                        Text(
                            text = "Respondendo a $replyingToName",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    ExpandableText(text = comment.content, key = comment.id)
                }
            }

            TextButton(
                onClick = { onReply(comment) },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            ) {
                Text("Responder", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun ExpandableText(text: String, key: String) {
    var expanded by rememberSaveable(key) { mutableStateOf(false) }
    var overflowing by remember(key) { mutableStateOf(false) }

    Column {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (expanded) Int.MAX_VALUE else 5,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { if (!expanded) overflowing = it.hasVisualOverflow },
            modifier = Modifier.animateContentSize(),
        )
        if (overflowing || expanded) {
            Text(
                text = if (expanded) "Ler menos" else "Ler mais",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(top = 2.dp),
            )
        }
    }
}