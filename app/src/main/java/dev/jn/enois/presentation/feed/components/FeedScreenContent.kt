package dev.jn.enois.presentation.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.domain.model.ReactionSummary
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.presentation.media.MediaUiState
import dev.jn.enois.presentation.reactions.PostReactionsState
import dev.jn.enois.presentation.reactions.ReactionAction

@Composable
fun FeedScreenContent(
    posts: LazyPagingItems<FeedPost>,
    listState: LazyListState,
    innerPadding: PaddingValues,
    mediaUiState: MediaUiState,
    reactionStates: Map<String, PostReactionsState>,
    onReactionAction: (ReactionAction) -> Unit,
    onLoadMedia: (List<String>) -> Unit,
    onProvideReactionSummary: (String, ReactionSummary, ReactionType?) -> Unit,
    onNavigateToPostDetails: (FeedPost) -> Unit,
    onNavigateToMediaViewer: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val contentPadding = remember(innerPadding) {
        PaddingValues(
            horizontal = 8.dp,
        ) + innerPadding
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier,
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                count = posts.itemCount,
                key = posts.itemKey { it.id },
                contentType = posts.itemContentType { "post" }) { index ->

                posts[index]?.let { post ->

                    val reactionState = reactionStates[post.id] ?: PostReactionsState()

                    FeedPostItem(
                        post = post,
                        mediaUiState = mediaUiState,
                        reactionState = reactionState,
                        onLoadMedia = onLoadMedia,
                        onProvideReactionSummary = { postId, summary ->
                            onProvideReactionSummary(
                                postId,
                                summary,
                                post.currentUserReaction
                            )
                        },
                        onPostClick = { onNavigateToPostDetails(post) },
                        onMediaClick = { onNavigateToMediaViewer(it) },
                        onReactionAction = onReactionAction
                    )
                }
            }

            item {
                when (posts.loadState.append) {
                    is LoadState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is LoadState.Error -> {
                        Button(onClick = posts::retry) {
                            Text(
                                text = "Retry"
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }

    }
}