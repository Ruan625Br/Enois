package dev.jn.enois.presentation.components.comments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.Comment
import dev.jn.enois.domain.util.buildThreads
import kotlinx.coroutines.flow.Flow

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
    val threads = remember(state.comments) { buildThreads(state.comments) }
    val namesById = remember(state.comments) { state.comments.associate { it.id to it.authorName } }

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

    val lineColor = MaterialTheme.colorScheme.outlineVariant


    Column(modifier) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
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
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    threads.forEach { thread ->
                        val hasReplies = thread.replies.isNotEmpty()

                        item(key = thread.root.id) {
                            CommentItem(
                                comment = thread.root,
                                onReply = onReply,
                                modifier = Modifier
                                    .threadLine(
                                        if (hasReplies) ThreadLine.RootWithReplies else ThreadLine.None,
                                        lineColor
                                    )

                            )
                        }
                        itemsIndexed(
                            thread.replies,
                            key = { _, reply -> reply.id }) { index, reply ->
                            val isLast = index == thread.replies.lastIndex
                            CommentItem(
                                comment = reply,
                                onReply = onReply,
                                replyingToName = reply.parentCommentId?.let(namesById::get),
                                modifier = Modifier
                                    .threadLine(
                                        if (isLast) ThreadLine.LastReply else ThreadLine.Reply,
                                        lineColor
                                    )
                                    .padding(
                                        start = ReplyIndent,
                                        top = CommentItemVerticalPadding,
                                        bottom = CommentItemVerticalPadding,
                                    ),
                            )
                        }

                        item(key = "gap-${thread.root.id}") { Spacer(Modifier.height(12.dp)) }

                    }


                    if (state.nextCursor != null) {
                        item(key = "footer") {
                            if (state.error == null) {
                                LaunchedEffect(Unit) { onLoadMore() }
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp), Alignment.Center
                                ) {
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