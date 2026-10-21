package dev.jn.enois.presentation.feed.components

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest

@Composable
fun UserAvatar(
    url: String,
    modifier: Modifier = Modifier
) {

    val context = LocalPlatformContext.current

    val urlResolved = remember(url) {
        resolveCdnUrl(url)
    }

    val model = remember(context, urlResolved) {
        ImageRequest.Builder(context)
            .data(urlResolved)
            .build()
    }

    AsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier
            .size(50.dp)
            .clip(MaterialTheme.shapes.medium),
        onError = { state ->
            Log.e(
                "UserAvatar",
                "Falha ao carregar $urlResolved - $url",
                state.result.throwable
            )
        }
    )
}

private fun resolveCdnUrl(url: String): String {
    val cleaned = url.replace("%2F", "/")

    if (cleaned.startsWith("http://") ||
        cleaned.startsWith("https://")
    ) {
        return cleaned
    }

    val baseUrl = "https://ik.imagekit.io/socialcoisa"
    val separator = if (cleaned.startsWith("/")) "" else "/"

    return "$baseUrl$separator$cleaned"
}