package dev.jn.enois.presentation.feed.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.presentation.reactions.ReactionAction
import dev.jn.enois.ui.icons.AppIcons

@Composable
fun PostActions(
    postId: String,
    userReaction: ReactionType?,
    reactionPending: Boolean,
    onAction: (ReactionAction) -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
    ) {
        PostReactionButton(
            postId = postId,
            userReaction = userReaction,
            reactionPending = reactionPending,
            onAction = onAction
        )
    }
}

@Composable
private fun PostActionItem(
    modifier: Modifier = Modifier
) {

}


@Composable
private fun PostReactionButton(
    postId: String,
    userReaction: ReactionType?,
    reactionPending: Boolean,
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
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedContent(
                    targetState = userReaction,
                    label = "reaction-count"
                ) { reaction ->
                    if (reaction != null) Text(reaction.emoji)
                    else Icon(AppIcons.AddReaction, contentDescription = "Reagir")

                }
            }
        }
    }
}