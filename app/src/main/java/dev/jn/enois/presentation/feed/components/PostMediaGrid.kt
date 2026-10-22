package dev.jn.enois.presentation.feed.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import dev.jn.enois.domain.model.Media
import dev.jn.enois.presentation.components.avatar.resolveCdnUrl
import dev.jn.enois.presentation.media.MediaLoadState
import dev.jn.enois.presentation.media.MediaUiState
import dev.jn.enois.presentation.media.viewer.mediaSharedBounds

@Composable
fun PostMediaGrid(
    mediaIds: List<String>,
    mediaUiState: MediaUiState,
    onLoadMedia: (List<String>) -> Unit,
    onMediaClick: (Media) -> Unit,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(mediaIds) {
        onLoadMedia(mediaIds)
    }

    val mediaItems = mediaIds.mapNotNull { id ->
        when (val state = mediaUiState.mediaById[id]) {
            is MediaLoadState.Success -> state.media
            else -> null
        }
    }

    FlowRow(
        modifier = modifier
    ) {
        mediaItems.forEach { media ->
            MediaItem(
                media = media,
                onMediaClick = { onMediaClick(media) })
        }
    }
}

@Composable
private fun MediaItem(
    media: Media,
    onMediaClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium
) {

    val context = LocalPlatformContext.current

    val urlResolved = remember(media.cdnUrl) {
        resolveCdnUrl(media.cdnUrl.orEmpty())
    }

    val model = remember(context, urlResolved) {
        ImageRequest.Builder(context)
            .data(urlResolved)
            .build()
    }
    var failed by remember(urlResolved) { mutableStateOf(false) }
    var loading by remember(urlResolved) { mutableStateOf(true) }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(shape)
            .clickable(
                enabled = !failed && !loading
            ) {
                onMediaClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        if (loading || failed) {
            Text(
                text = "Carregando...",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (!failed) {
            AsyncImage(
                model = model,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .mediaSharedBounds(media.id),
                onLoading = { loading = true },
                onSuccess = { loading = false },
                onError = {
                    loading = false
                    failed = true
                },
            )
        }
    }
}