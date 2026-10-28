package dev.jn.enois.presentation.chat.thread.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dev.jn.enois.domain.model.UserProfile
import dev.jn.enois.presentation.animation.LocalSharedTransitionScope
import dev.jn.enois.presentation.components.avatar.UserAvatar
import dev.jn.enois.presentation.feed.components.FeedTopFadeOverlay
import dev.jn.enois.presentation.feed.components.toolbarSharedBounds
import dev.jn.enois.ui.icons.AppIcons

@Composable
fun ChatToolbar(
    title: String,
    profile: UserProfile?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .toolbarSharedBounds(
                sharedTransitionScope = LocalSharedTransitionScope.current,
                animatedVisibilityScope = LocalNavAnimatedContentScope.current
            )
            .fillMaxWidth()
    ) {
        FeedTopFadeOverlay(
            modifier = Modifier
        )
        Row(
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                    horizontal = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ToolbarLeftButtons(
                onBack = onBack
            ) {
                UserAvatar(
                    modifier = Modifier
                        .userAvatarSharedBounds()
                        .size(48.dp * 0.75f),
                    url = profile?.avatar.orEmpty(),
                    userName = profile?.name.orEmpty(),
                    shape = CircleShape,

                    )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .chatTitleSharedBounds()
            )
        }


    }
}

@Composable
private fun ToolbarLeftButtons(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    val pillSize = 48.dp
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
        ),
        modifier = modifier
            .height(pillSize)
            .animateContentSize(),
    ) {
        Row(
            modifier = Modifier
                .padding(end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = onBack
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier
                        .padding(end = 5.dp),
                    imageVector = AppIcons.ArrowBackIosNew,
                    contentDescription = null,
                )
            }

            content()
        }
    }
}


@Composable
fun Modifier.userAvatarSharedBounds(
    sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedContentScope.current,
): Modifier = with(sharedTransitionScope) {
    this@userAvatarSharedBounds.sharedBounds(
        sharedContentState = rememberSharedContentState(
            key = "userAvatar"
        ),
        animatedVisibilityScope = animatedVisibilityScope,
        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
    )
}

@Composable
fun Modifier.chatTitleSharedBounds(
    sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedContentScope.current,
): Modifier = with(sharedTransitionScope) {
    this@chatTitleSharedBounds.sharedBounds(
        sharedContentState = rememberSharedContentState(
            key = "chat-title"
        ),
        animatedVisibilityScope = animatedVisibilityScope,
        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
    )
}

