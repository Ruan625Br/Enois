package dev.jn.enois.presentation.feed.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dev.jn.enois.core.util.formatTime
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.domain.model.ReactionSummary
import dev.jn.enois.presentation.animation.LocalSharedTransitionScope
import dev.jn.enois.presentation.components.avatar.UserAvatar
import dev.jn.enois.presentation.components.badge.UserBadges
import dev.jn.enois.presentation.components.richtext.CollapsibleRichText
import dev.jn.enois.presentation.components.styledname.StyledName
import dev.jn.enois.presentation.media.MediaUiState
import dev.jn.enois.presentation.reactions.PostReactionsState
import dev.jn.enois.presentation.reactions.ReactionAction


@Composable
fun FeedPostItem(
    post: FeedPost,
    onPostClick: () -> Unit,
    onMediaClick: (String) -> Unit,
    onReactionAction: (ReactionAction) -> Unit,
    onCommentClick: () -> Unit,
    mediaUiState: MediaUiState,
    reactionState: PostReactionsState,
    onLoadMedia: (List<String>) -> Unit,
    onProvideReactionSummary: (String, ReactionSummary) -> Unit,
    modifier: Modifier = Modifier,
    showFullPost: Boolean = false
) {

    LaunchedEffect(post.id, post.reactionSummary) {
        post.reactionSummary?.let { summary ->
            onProvideReactionSummary(post.id, summary)
        }
    }


    FeedPostItemContent(
        post = post,
        onPostClick = onPostClick,
        onMediaClick = onMediaClick,
        onReactionAction = onReactionAction,
        onCommentClick = onCommentClick,
        mediaUiState = mediaUiState,
        reactionState = reactionState,
        onLoadMedia = onLoadMedia,
        modifier = modifier,
        showFullPost = showFullPost
    )
}

@Composable
fun FeedPostItemContent(
    post: FeedPost,
    onPostClick: () -> Unit,
    onMediaClick: (String) -> Unit,
    onReactionAction: (ReactionAction) -> Unit,
    onCommentClick: () -> Unit,
    mediaUiState: MediaUiState,
    reactionState: PostReactionsState,
    onLoadMedia: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    showFullPost: Boolean = false,
) {

    val badges = remember(post) {
        post.getBadges()
    }

    val formattedTime = remember(post.createdAt) {
        formatTime(post.createdAt)
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .postSharedBounds(
                postId = post.id,
                sharedTransitionScope = LocalSharedTransitionScope.current,
                animatedVisibilityScope = LocalNavAnimatedContentScope.current
            )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PostHeader(
                post = post,
                badges = badges,
                formattedTime = formattedTime
            )

            CollapsibleRichText(
                content = post.content,
                key = post.id,
                onMention = {},
            )

            PostMediaGrid(
                mediaIds = post.mediaIds,
                mediaUiState = mediaUiState,
                onLoadMedia = onLoadMedia,
                onMediaClick = {
                    onMediaClick(it.id)
                }
            )


            Column {
                ReactionSummary(
                    summary = reactionState.summary,
                    userReaction = reactionState.userReaction,
                    reactionPending = reactionState.isPending,
                    onReactionClick = {
                        onReactionAction(
                            ReactionAction.Click(
                                postId = post.id,
                                type = it
                            )
                        )
                    }
                )

                HorizontalDivider()

                PostActions(
                    postId = post.id,
                    userReaction = reactionState.userReaction,
                    reactionPending = reactionState.isPending,
                    counters = post.counters,
                    onAction = onReactionAction,
                    onCommentClick = onCommentClick
                )
            }
        }
    }

}

@Composable
private fun PostHeader(
    post: FeedPost,
    badges: List<String>,
    formattedTime: String,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        UserAvatar(
            url = post.authorAvatar.orEmpty(),
            userName = post.authorName,
            frame = post.authorAvatarFrame
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 48.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Column {
                StyledName(
                    name = post.authorName,
                    style = post.authorStyle
                )

                UserBadges(badges = badges)
            }


            Spacer(
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = "· $formattedTime",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }
    }
}


@Composable
fun Modifier.postSharedBounds(
    postId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
): Modifier = with(sharedTransitionScope) {
    this@postSharedBounds.sharedBounds(
        sharedContentState = rememberSharedContentState(
            key = "post-card-$postId"
        ),
        animatedVisibilityScope = animatedVisibilityScope,
        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
    )
}