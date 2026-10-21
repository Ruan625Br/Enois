package dev.jn.enois.presentation.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.presentation.feed.components.FeedScreenContent
import dev.jn.enois.presentation.feed.components.FeedToolbar
import org.koin.androidx.compose.koinViewModel


@Composable
fun FeedRoute(
    onNavigateToPostDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel(),
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val posts = viewModel.posts.collectAsLazyPagingItems()

    FeedScreen(
        uiState = uiState.value,
        posts = posts,
        onNavigateToPostDetails = { post ->
            viewModel.addPostToCache(post)
            onNavigateToPostDetails(post.id)
        },
        modifier = modifier
    )
}

@Composable
private fun FeedScreen(
    uiState: FeedUiState,
    posts: LazyPagingItems<FeedPost>,
    onNavigateToPostDetails: (FeedPost) -> Unit,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyListState()
    var toolbarVisible by remember { mutableStateOf(true) }

    LaunchedEffect(listState) {
        var previousIndex = listState.firstVisibleItemIndex
        var previousOffset = listState.firstVisibleItemScrollOffset

        snapshotFlow {
            listState.firstVisibleItemIndex to
                    listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            val delta =
                (index - previousIndex) * 1000 +
                        (offset - previousOffset)

            if (index == 0 && offset == 0) {
                toolbarVisible = true
            } else if (kotlin.math.abs(delta) > 4) {
                toolbarVisible = delta < 0
            }
            previousIndex = index
            previousOffset = offset
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AnimatedVisibility(
                visible = toolbarVisible,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                FeedToolbar(
                    userProfile = uiState.userProfile
                )
            }
        },
    ) { innerPadding ->

        FeedScreenContent(
            posts = posts,
            listState = listState,
            innerPadding = innerPadding,
            onNavigateToPostDetails = onNavigateToPostDetails,
            modifier = modifier
        )
    }
}