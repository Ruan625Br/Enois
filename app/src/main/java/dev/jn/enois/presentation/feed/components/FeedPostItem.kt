package dev.jn.enois.presentation.feed.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dev.jn.enois.core.util.formatTime
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.presentation.animation.LocalSharedTransitionScope
import dev.jn.enois.presentation.components.avatar.UserAvatar
import dev.jn.enois.presentation.components.badge.UserBadges

private const val POST_COMPACT_THRESHOLD = 400
private const val POST_MAX_LINES = 15

@Composable
fun FeedPostItem(
    post: FeedPost,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
    showFullPost: Boolean = false
) {


    FeedPostItemContent(
        post = post,
        onPostClick = onPostClick,
        modifier = modifier,
        showFullPost = showFullPost
    )
}

@Composable
fun FeedPostItemContent(
    post: FeedPost,
    onPostClick: () -> Unit,
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
                overflow = TextOverflow.Ellipsis
            )

            if (isCompact) {
                TextButton(onClick = onPostClick) {
                    Text("Ler mais")
                }
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