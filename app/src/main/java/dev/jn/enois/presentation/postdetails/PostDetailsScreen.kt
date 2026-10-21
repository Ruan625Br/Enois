package dev.jn.enois.presentation.postdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.jn.enois.presentation.feed.components.FeedPostItem
import dev.jn.enois.presentation.postdetails.components.PostDetailsToolbar
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun PostDetailRoute(
    postId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel: PostDetailsViewModel = koinViewModel {
        parametersOf(postId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PostDetailsScreen(
        uiState = uiState,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun PostDetailsScreen(
    uiState: PostDetailsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            PostDetailsToolbar(
                onBack = onBack
            )
        }
    ) { innerPadding ->
        val contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = 0.dp
        ) + innerPadding
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.post != null) {
                FeedPostItem(
                    post = uiState.post,
                    showFullPost = true,
                    onPostClick = {}
                )

            }
        }
    }
}

