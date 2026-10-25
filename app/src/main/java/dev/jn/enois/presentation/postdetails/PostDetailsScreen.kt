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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.jn.enois.domain.model.ReactionSummary
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.presentation.components.comments.CommentsSheet
import dev.jn.enois.presentation.feed.components.FeedPostItem
import dev.jn.enois.presentation.media.MediaUiState
import dev.jn.enois.presentation.media.MediaViewModel
import dev.jn.enois.presentation.postdetails.components.PostDetailsToolbar
import dev.jn.enois.presentation.reactions.PostReactionsState
import dev.jn.enois.presentation.reactions.ReactionAction
import dev.jn.enois.presentation.reactions.ReactionViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun PostDetailRoute(
    postId: String,
    onBack: () -> Unit,
    onNavigateToMediaViewer: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    val viewModel: PostDetailsViewModel = koinViewModel {
        parametersOf(postId)
    }
    val mediaViewModel: MediaViewModel = koinViewModel()
    val reactionViewModel: ReactionViewModel = koinViewModel()


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mediaUiState by mediaViewModel.uiState.collectAsStateWithLifecycle()
    val reactionStates by reactionViewModel.states.collectAsStateWithLifecycle()

    PostDetailsScreen(
        uiState = uiState,
        onBack = onBack,
        mediaUiState = mediaUiState,
        reactionStates = reactionStates,
        onLoadMedia = mediaViewModel::loadMedia,
        onNavigateToMediaViewer = onNavigateToMediaViewer,
        onProvideReactionSummary = reactionViewModel::provideFeedSummary,
        onReactionAction = reactionViewModel::onReactionAction,
        modifier = modifier
    )
}

@Composable
private fun PostDetailsScreen(
    uiState: PostDetailsUiState,
    mediaUiState: MediaUiState,
    reactionStates: Map<String, PostReactionsState>,
    onNavigateToMediaViewer: (String) -> Unit,
    onLoadMedia: (List<String>) -> Unit,
    onReactionAction: (ReactionAction) -> Unit,
    onProvideReactionSummary: (String, ReactionSummary, ReactionType?) -> Unit,
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

        var commentsPostId by rememberSaveable { mutableStateOf<String?>(null) }


        Column(
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
        ) {
            val post = uiState.post
            if (post != null) {
                val reactionState = reactionStates[post.id] ?: PostReactionsState()

                FeedPostItem(
                    post = post,
                    mediaUiState = mediaUiState,
                    reactionState = reactionState,
                    onLoadMedia = onLoadMedia,
                    onProvideReactionSummary = { postId, summary ->
                        onProvideReactionSummary(postId, summary, post.currentUserReaction)
                    },
                    showFullPost = true,
                    onPostClick = {},
                    onReactionAction = onReactionAction,
                    onMediaClick = onNavigateToMediaViewer,
                    onCommentClick = {
                        commentsPostId = post.id
                    }
                )

            }
        }

        commentsPostId?.let { postId ->
            CommentsSheet(
                postId = postId,
                onDismiss = { commentsPostId = null },
                onCommentSent = { /* soma +1 na contagem do post, ver nota abaixo */ },
            )
        }
    }
}

