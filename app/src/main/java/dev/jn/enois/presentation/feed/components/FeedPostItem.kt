package dev.jn.enois.presentation.feed.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dev.jn.enois.core.util.formatTime
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.domain.model.ReactionSummary
import dev.jn.enois.presentation.animation.LocalSharedTransitionScope
import dev.jn.enois.presentation.components.avatar.UserAvatar
import dev.jn.enois.presentation.components.badge.UserBadges
import dev.jn.enois.presentation.media.MediaUiState
import dev.jn.enois.presentation.reactions.PostReactionsState
import dev.jn.enois.presentation.reactions.ReactionAction
import dev.mttmcknn.seaglass.SeaGlassButton
import dev.mttmcknn.seaglass.SeaGlassShape
import dev.mttmcknn.seaglass.SeaGlassTheme

private const val POST_COMPACT_THRESHOLD = 400
private const val POST_MAX_LINES = 15

@Composable
fun FeedPostItem(
    post: FeedPost,
    onPostClick: () -> Unit,
    onMediaClick: (String) -> Unit,
    onReactionAction: (ReactionAction) -> Unit,
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
    mediaUiState: MediaUiState,
    reactionState: PostReactionsState,
    onLoadMedia: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    showFullPost: Boolean = false,
) {

    val isCompact = !showFullPost &&
            post.content.length > POST_COMPACT_THRESHOLD

    val badges = remember(post) {
        post.getBadges()
    }

    val formattedTime = remember(post.createdAt) {
        formatTime(post.createdAt)
    }

    val glassSeed = remember(post.id) { post.id.hashCode() }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
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
            Text(
                text = post.content,
                maxLines = if (isCompact) {
                    POST_MAX_LINES
                } else {
                    Int.MAX_VALUE
                },
                fontFamily = FontFamily.Serif,
                overflow = TextOverflow.Ellipsis
            )

            if (isCompact) {
                SeaGlassButton(
                    onClick = onPostClick,
                    shape = SeaGlassShape.Capsule,
                    style = SeaGlassTheme.componentStyle.withSeed(glassSeed + 7),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                ) {
                    Text("Ler mais", style = MaterialTheme.typography.labelLarge)
                }
            }

            PostMediaGrid(
                mediaIds = post.mediaIds,
                mediaUiState = mediaUiState,
                onLoadMedia = onLoadMedia,
                onMediaClick = {
                    onMediaClick(it.id)
                }
            )

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
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Column {
                Text(
                    text = post.authorName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.onSurface
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
                fontFamily = FontFamily.Serif,
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