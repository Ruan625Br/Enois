package dev.jn.enois.presentation.feed.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dev.jn.enois.domain.model.UserProfile
import dev.jn.enois.presentation.animation.LocalSharedTransitionScope
import dev.jn.enois.ui.icons.AppIcons

@Composable
fun FeedToolbar(
    userProfile: UserProfile?,
    modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .toolbarSharedBounds(
                sharedTransitionScope = LocalSharedTransitionScope.current,
                animatedVisibilityScope = LocalNavAnimatedContentScope.current
            )
    ) {
        FeedTopFadeOverlay(
            modifier = Modifier
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp,
                    horizontal = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FeedToolbarDrawerButton()

            Spacer(Modifier.weight(1f))

            FeedToolbarActions(
                userProfile = userProfile
            )
        }


    }
}

@Composable
private fun FeedToolbarDrawerButton(modifier: Modifier = Modifier) {
    Surface(
        onClick = {},
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
        ),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = AppIcons.RoundedMenu,
                contentDescription = null,
            )
        }
    }
}


@Composable
fun Modifier.toolbarSharedBounds(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
): Modifier = with(sharedTransitionScope) {
    this@toolbarSharedBounds.sharedBounds(
        sharedContentState = rememberSharedContentState(
            key = "toolbar"
        ),
        animatedVisibilityScope = animatedVisibilityScope,
        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
    )
}


