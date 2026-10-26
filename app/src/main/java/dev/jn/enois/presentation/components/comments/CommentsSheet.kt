package dev.jn.enois.presentation.components.comments

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsSheet(
    postId: String,
    onDismiss: () -> Unit,
    onCommentSent: (postId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommentsViewModel = koinViewModel(),
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(postId) { viewModel.load(postId) }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        CommentsContent(
            state = state,
            events = viewModel.events,
            onLoadMore = viewModel::loadMore,
            onRetry = viewModel::retry,
            onSend = viewModel::send,
            onReply = viewModel::onReply,
            onCancelReply = viewModel::cancelReply,
            onCommentSent = onCommentSent,
            modifier = Modifier
                .fillMaxHeight()
                .navigationBarsPadding()
                .imePadding(),
        )
    }
}