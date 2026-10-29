package dev.jn.enois.presentation.reactions

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedReactionCount(
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