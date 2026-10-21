package dev.jn.enois.presentation.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FeedTopFadeOverlay(
    modifier: Modifier = Modifier
) {
    val statusBarHeight =
        WindowInsets.statusBars
            .asPaddingValues()
            .calculateTopPadding()

    val backgroundColor = MaterialTheme.colorScheme.background

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(statusBarHeight)
                .background(backgroundColor)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to backgroundColor,
                            0.35f to backgroundColor.copy(alpha = 0.92f),
                            0.70f to backgroundColor.copy(alpha = 0.55f),
                            1.0f to Color.Transparent
                        )
                    )
                )
        )
    }
}

