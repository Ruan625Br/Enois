package dev.jn.enois.presentation.chat.thread.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.presentation.reactions.AnimatedReactionCount

@Composable
fun MessageReactions(
    reactions: Map<ReactionType, Int>,
    myReaction: ReactionType?,
    enabled: Boolean,
    onToggle: (ReactionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (reactions.isEmpty()) return
    val sorted = remember(reactions) { reactions.entries.sortedBy { it.key.ordinal } }
    val shape =  CircleShape

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        sorted.forEach { (type, count) ->
            val mine = type == myReaction
            Box(
                modifier = Modifier
                    .clip(shape)
                    .clickable(
                        enabled = enabled,
                        onClick = { onToggle(type) }
                    )
                    .background(
                        if (mine) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        else MaterialTheme.colorScheme.surfaceContainerHighest,
                    )
                    .then(
                        if (mine) Modifier.border(
                            BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            ),
                            shape
                        ) else Modifier
                    )

            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(type.emoji, style = MaterialTheme.typography.labelMedium)
                    AnimatedReactionCount(
                        count = count,
                        color = if (mine) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}