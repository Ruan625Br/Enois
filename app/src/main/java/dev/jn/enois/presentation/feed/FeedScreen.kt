package dev.jn.enois.presentation.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.domain.model.ReactionSummary
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.presentation.components.avatar.fx.ProvideAvatarFxClock
import dev.jn.enois.presentation.feed.components.FeedScreenContent
import dev.jn.enois.presentation.feed.components.FeedToolbar
import dev.jn.enois.presentation.media.MediaUiState
import dev.jn.enois.presentation.media.MediaViewModel
import dev.jn.enois.presentation.reactions.PostReactionsState
import dev.jn.enois.presentation.reactions.ReactionAction
import dev.jn.enois.presentation.reactions.ReactionViewModel
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel


@Composable
fun FeedRoute(
    onNavigateToPostDetails: (String) -> Unit,
    onNavigateToMediaViewer: (String) -> Unit,
    modifier: Modifier = Modifier,
    feedViewModel: FeedViewModel = koinViewModel(),
    mediaViewModel: MediaViewModel = koinViewModel(),
    reactionViewModel: ReactionViewModel = koinViewModel()
) {

    val feedUiState by feedViewModel.uiState.collectAsStateWithLifecycle()
    val mediaUiState by mediaViewModel.uiState.collectAsStateWithLifecycle()
    val posts = feedViewModel.posts.collectAsLazyPagingItems()
    val reactionStates by reactionViewModel.states.collectAsStateWithLifecycle()

    FeedScreen(
        feedUiState = feedUiState,
        mediaUiState = mediaUiState,
        reactionStates = reactionStates,
        posts = posts,
        onNavigateToPostDetails = { post ->
            feedViewModel.addPostToCache(post)
            onNavigateToPostDetails(post.id)
        },
        onNavigateToMediaViewer = onNavigateToMediaViewer,
        onLoadMedia = mediaViewModel::loadMedia,
        onProvideReactionSummary = reactionViewModel::provideFeedSummary,
        onReactionAction = reactionViewModel::onReactionAction,
        modifier = modifier
    )
}

@Composable
private fun FeedScreen(
    feedUiState: FeedUiState,
    mediaUiState: MediaUiState,
    reactionStates: Map<String, PostReactionsState>,
    onReactionAction: (ReactionAction) -> Unit,
    onLoadMedia: (List<String>) -> Unit,
    onProvideReactionSummary: (String, ReactionSummary, ReactionType?) -> Unit,
    posts: LazyPagingItems<FeedPost>,
    onNavigateToPostDetails: (FeedPost) -> Unit,
    onNavigateToMediaViewer: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyListState()

    var toolbarHeightPx by remember { mutableFloatStateOf(0f) }
    var toolbarOffsetPx by remember { mutableFloatStateOf(0f) }

    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                toolbarOffsetPx = (toolbarOffsetPx + available.y).coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.canScrollBackward }
            .filter { !it }
            .collect { toolbarOffsetPx = 0f }
    }

    ProvideAvatarFxClock(
        paused = { listState.isScrollInProgress }) {


        Scaffold(
            modifier = modifier.nestedScroll(scrollConnection),
            topBar = {
                Box(
                    Modifier
                        .onSizeChanged { toolbarHeightPx = it.height.toFloat() }
                        .graphicsLayer {
                            translationY = toolbarOffsetPx
                            alpha =
                                if (toolbarHeightPx > 0f) 1f + toolbarOffsetPx / toolbarHeightPx else 1f
                        },
                ) {
                    FeedToolbar(
                        userProfile = feedUiState.userProfile
                    )
                }
            },
        ) { innerPadding ->

            FeedScreenContent(
                posts = posts,
                listState = listState,
                innerPadding = innerPadding,
                mediaUiState = mediaUiState,
                reactionStates = reactionStates,
                onReactionAction = onReactionAction,
                onLoadMedia = onLoadMedia,
                onProvideReactionSummary = onProvideReactionSummary,
                onNavigateToPostDetails = onNavigateToPostDetails,
                onNavigateToMediaViewer = onNavigateToMediaViewer
            )
        }
    }
}