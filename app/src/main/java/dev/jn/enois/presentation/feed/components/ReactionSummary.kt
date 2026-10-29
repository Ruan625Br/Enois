package dev.jn.enois.presentation.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.ReactionSummary
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.presentation.reactions.AnimatedReactionCount

@Composable
fun ReactionSummary(
    summary: ReactionSummary?,
    userReaction: ReactionType?,
    reactionPending: Boolean,
    onReactionClick: (ReactionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val counts = summary?.counts.orEmpty()
        .filterValues { it > 0 }

    if (counts.isEmpty()) return

    var reactionOrder by remember {
        mutableStateOf(emptyList<ReactionType>())
    }

    LaunchedEffect(counts.keys) {
        reactionOrder = stableReactionOrder(
            counts = counts,
            previousOrder = reactionOrder,
        )
    }

    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        reactionOrder.forEach { type ->
            val selected = userReaction == type
            val count = counts[type] ?: 0

            ElevatedFilterChip(
                selected = selected,
                onClick = {
                    if (!reactionPending) {
                        onReactionClick(type)
                    }
                },
                shape = MaterialTheme.shapes.large,
                label = {
                    Row(
                        modifier = Modifier
                            .padding(
                                vertical = 6.dp,
                            ),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = type.emoji)

                        AnimatedReactionCount(
                            count = count,
                            color = if (selected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }

                        )
                    }
                },

                )
        }
    }
}


private fun stableReactionOrder(
    counts: Map<ReactionType, Int>,
    previousOrder: List<ReactionType>,
): List<ReactionType> {
    val activeTypes = counts
        .filterValues { it > 0 }
        .keys

    val retained = previousOrder.filter { it in activeTypes }

    val newTypes = (activeTypes - retained.toSet())
        .sortedBy { it.name }

    return retained + newTypes
}