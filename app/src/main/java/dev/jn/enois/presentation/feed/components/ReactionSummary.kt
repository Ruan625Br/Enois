package dev.jn.enois.presentation.feed.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.ReactionSummary
import dev.jn.enois.domain.model.ReactionType

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


    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        counts.forEach { (type, count) ->
            val selected = userReaction == type

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

@Composable
private fun AnimatedReactionCount(
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = count,
        modifier = modifier,
        transitionSpec = {
            val direction = if (targetState > initialState) 1 else -1

            (slideInVertically { height -> direction * height / 2 } +
                    fadeIn())
                .togetherWith(
                    slideOutVertically { height -> -direction * height / 2 } +
                            fadeOut()
                )
                .using(
                    SizeTransform(clip = false)
                )
        },
        label = "reaction-count",
    ) { animatedCount ->
        Text(
            text = animatedCount.toString(),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            color = color,
        )
    }
}