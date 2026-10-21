package dev.jn.enois.presentation.feed

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.presentation.feed.components.FeedScreenContent
import org.koin.androidx.compose.koinViewModel


@Composable
fun FeedRoute(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel(),
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val posts = viewModel.posts.collectAsLazyPagingItems()

    FeedScreen(
        uiState = uiState.value,
        posts = posts,
        modifier = modifier
    )
}

@Composable
private fun FeedScreen(
    uiState: FeedUiState,
    posts: LazyPagingItems<FeedPost>,
    modifier: Modifier = Modifier
) {

    FeedScreenContent(
        posts = posts,
        modifier = modifier
    )
}