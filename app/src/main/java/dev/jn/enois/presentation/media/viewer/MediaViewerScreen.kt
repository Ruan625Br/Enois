package dev.jn.enois.presentation.media.viewer

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import dev.jn.enois.presentation.animation.LocalSharedTransitionScope
import dev.jn.enois.presentation.media.MediaLoadState
import dev.jn.enois.presentation.media.MediaUiState
import dev.jn.enois.presentation.media.MediaViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MediaViewerRoute(
    mediaId: String,
    modifier: Modifier = Modifier,
    mediaViewModel: MediaViewModel = koinViewModel()
) {
    val uiState by mediaViewModel.uiState.collectAsState()

    LaunchedEffect(mediaId) {
        mediaViewModel.loadMedia(listOf(mediaId))
    }

    MediaViewerScreen(
        mediaId = mediaId,
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
private fun MediaViewerScreen(
    mediaId: String,
    uiState: MediaUiState,
    modifier: Modifier = Modifier
) {

    val media = (uiState.mediaById[mediaId] as? MediaLoadState.Success)
        ?.media

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        if (media == null) {
            CircularProgressIndicator()
        } else {
            AsyncImage(
                model = media.cdnUrl,
                contentDescription = "Imagem em tamanho ampliado",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .mediaSharedBounds(mediaId),
            )
        }
    }
}


@Composable
fun Modifier.mediaSharedBounds(
    mediaId: String,
    sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedContentScope.current,
): Modifier = with(sharedTransitionScope) {
    this@mediaSharedBounds.sharedBounds(
        sharedContentState = rememberSharedContentState(
            key = "media-$mediaId"
        ),
        animatedVisibilityScope = animatedVisibilityScope,
        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
    )
}


