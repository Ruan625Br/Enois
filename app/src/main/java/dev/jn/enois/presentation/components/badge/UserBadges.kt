package dev.jn.enois.presentation.components.badge

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import dev.jn.enois.presentation.components.avatar.resolveCdnUrl

@Composable
fun UserBadges(
    badges: List<String>,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        badges.forEach { badge ->
            if (badge.isBlank()) return@forEach
            UserBadge(
                url = badge,
                shape = shape
            )
        }
    }
}


@Composable
private fun UserBadge(
    url: String,
    shape: Shape,
    modifier: Modifier = Modifier) {

    val context = LocalPlatformContext.current

    val urlResolved = remember(url) {
        resolveCdnUrl(url)
    }

    val model = remember(context, urlResolved) {
        ImageRequest.Builder(context)
            .data(urlResolved)
            .build()
    }

    Box(
        modifier
            .size(15.dp)
            .clip(shape)
    ) {
        AsyncImage(
            model = model,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            onError = {
                Log.e("UseBadge", "Error loading image", it.result.throwable)
            }
        )
    }
}