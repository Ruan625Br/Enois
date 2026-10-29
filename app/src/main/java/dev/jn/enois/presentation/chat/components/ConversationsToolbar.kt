package dev.jn.enois.presentation.chat.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dev.jn.enois.presentation.animation.LocalSharedTransitionScope
import dev.jn.enois.presentation.feed.components.FeedTopFadeOverlay
import dev.jn.enois.presentation.feed.components.toolbarSharedBounds
import dev.jn.enois.presentation.postdetails.components.ToolbarBackButton

@Composable
fun ConversationsToolbar(
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolbarBackButton(
                onBack = onBack
            )
        }

        Text(
            text = "Conversas",
            modifier = Modifier
                .align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge
        )

    }
}