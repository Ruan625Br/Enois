package dev.jn.enois.presentation.components.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.Comment
import dev.jn.enois.domain.util.flattenComments
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun CommentsContent(
    state: CommentsUiState,
    events: Flow<CommentsEvent>,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onSend: (String) -> Unit,
    onReply: (Comment) -> Unit,
    onCancelReply: () -> Unit,
    onCommentSent: (postId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val inputState = rememberTextFieldState()
    val lineColors = rememberThreadLineColors(colored = false)
    var collapsed by rememberSaveable { mutableStateOf(emptySet<String>()) }
    val flat = remember(state.comments, collapsed) { flattenComments(state.comments, collapsed) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is CommentsEvent.Sent -> {
                    inputState.clearText()
                    onCommentSent(event.postId)
                }
            }
        }
    }

    val toggle: (String) -> Unit = { id ->
        if (id in collapsed) {
            collapsed = collapsed - id
        } else {
            val index = flat.indexOfFirst { it.comment.id == id }
            if (index in 0 until listState.firstVisibleItemIndex) {
                scope.launch { listState.scrollToItem(index) }
            }
            collapsed = collapsed + id
        }
    }

    LaunchedEffect(state.replyingTo?.id) {
        state.replyingTo?.let { collapsed = collapsed - it.id }
    }

    Column(modifier) {
        Box(Modifier
            .weight(1f)
            .fillMaxWidth()) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null && state.comments.isEmpty() -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(state.error, color = MaterialTheme.colorScheme.error)
                    OutlinedButton(onClick = onRetry) { Text("Tentar novamente") }
                }

                state.comments.isEmpty() -> Text(
                    text = "Seja a primeira pessoa a comentar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    itemsIndexed(flat, key = { _, node -> node.comment.id }) { index, node ->
                        CommentItem(
                            node = node,
                            onReply = onReply,
                            onToggle = toggle,
                            lineColors = lineColors,
                            modifier = Modifier
                                .animateItem()
                                .padding(top = if (node.depth == 0 && index > 0) 12.dp else 0.dp),
                        )
                    }

                    if (state.nextCursor != null) {
                        item(key = "footer") {
                            if (state.error == null) {
                                LaunchedEffect(Unit) { onLoadMore() }
                                Box(Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp), Alignment.Center) {
                                    CircularProgressIndicator(
                                        Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            } else {
                                TextButton(
                                    onClick = onLoadMore,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Tentar carregar mais")
                                }
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider()

        CommentInput(
            inputState = inputState,
            replyingTo = state.replyingTo,
            isSending = state.isSending,
            sendError = state.sendError,
            onSend = { onSend(inputState.text.toString()) },
            onCancelReply = {
                onCancelReply()
                inputState.clearText()
            },
        )
    }
}