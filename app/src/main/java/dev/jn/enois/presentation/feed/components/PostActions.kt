package dev.jn.enois.presentation.feed.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.PostCounters
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.presentation.reactions.ReactionAction
import dev.jn.enois.ui.icons.AppIcons

@Composable
fun PostActions(
    postId: String,
    userReaction: ReactionType?,
    reactionPending: Boolean,
    counters: PostCounters,
    onAction: (ReactionAction) -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PostReactionButton(
            postId = postId,
            userReaction = userReaction,
            count = counters.reactions,
            reactionPending = reactionPending,
            onAction = onAction
        )

        PostActionItem(
            icon = AppIcons.ChatBubble,
            onClick = onCommentClick,
            count = counters.comments
        )
    }
}

@Composable
private fun PostActionItem(
    icon: ImageVector,
    onClick: () -> Unit,
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp),
                imageVector = icon,
                contentDescription = null
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}


@Composable
private fun PostReactionButton(
    postId: String,
    userReaction: ReactionType?,
    reactionPending: Boolean,
    count: Int,
    onAction: (ReactionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    ReactionPressTarget(
        modifier = modifier,
        selected = userReaction,
        enabled = !reactionPending,
        onTap = { onAction(ReactionAction.Click(postId, userReaction ?: ReactionType.LOVE)) },
        onReact = { onAction(ReactionAction.Click(postId, it)) },
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                AnimatedContent(
                    targetState = userReaction,
                    label = "reaction-count"
                ) { reaction ->
                    if (reaction != null) {
                        Text(reaction.emoji)
                    } else {
                        Icon(
                            modifier = Modifier
                                .size(18.dp),
                            imageVector = AppIcons.AddReaction, contentDescription = "Reagir"
                        )
                    }

                }
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}