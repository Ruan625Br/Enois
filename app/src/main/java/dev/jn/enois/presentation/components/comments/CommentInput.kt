package dev.jn.enois.presentation.components.comments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.Comment
import dev.jn.enois.ui.icons.AppIcons

@Composable
fun CommentInput(
    inputState: TextFieldState,
    replyingTo: Comment?,
    isSending: Boolean,
    sendError: String?,
    onSend: () -> Unit,
    onCancelReply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(replyingTo?.id) {
        if (replyingTo != null) {
            val mention = replyingTo.authorUsername?.let { "@$it " }
            if (mention != null && inputState.text.isBlank()) {
                inputState.setTextAndPlaceCursorAtEnd(mention)
            }
            focusRequester.requestFocus()
        }
    }

    Column(modifier) {
        AnimatedVisibility(visible = replyingTo != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Respondendo a ${replyingTo?.authorName.orEmpty()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onCancelReply) {
                    Icon(AppIcons.Close, contentDescription = "Cancelar resposta")
                }
            }
        }

        if (sendError != null) {
            Text(
                text = sendError,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            CommentTextField(
                state = inputState,
                focusRequester = focusRequester,
                sendButtonContent = {
                    AnimatedVisibility(
                        visible = inputState.text.isNotBlank() && !isSending,
                        enter = slideInHorizontally(
                            initialOffsetX = { it / 2 }
                        ) + fadeIn(),
                        exit = slideOutHorizontally(
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                            targetOffsetX = { 200 }
                        ) + fadeOut()
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(36.dp),
                            onClick = onSend,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = AppIcons.ArrowUpward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
            )
            /*  OutlinedTextField(
                  uiState = inputState,
                  modifier = Modifier.weight(1f).focusRequester(focusRequester),
                  placeholder = { Text("Escreva um comentário") },
                  shape = MaterialTheme.shapes.extraLarge,
                  lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 4),
                  inputTransformation = InputTransformation.maxLength(800),
                  keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
              )
              FilledIconButton(
                  onClick = onSend,
                  enabled = inputState.text.isNotBlank() && !isSending,
              ) {
                  if (isSending) {
                      CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                  } else {
                      Icon(AppIcons.ArrowBackIosNew, contentDescription = "Enviar")
                  }
              }

             */
        }
    }
}

@Composable
private fun RowScope.CommentTextField(
    state: TextFieldState,
    focusRequester: FocusRequester,
    sendButtonContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {

    var visualLineCount by remember { mutableIntStateOf(1) }

    InputCapsule(
        modifier = modifier
            .weight(1f)
    ) {
        Column {
            Box {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 1.dp)
                        .focusRequester(focusRequester),
                    state = state,
                    onTextLayout = { getResult ->
                        val result = getResult()
                        if (result != null) {
                            visualLineCount = result.lineCount
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Escreva um comentário",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 5),
                    inputTransformation = InputTransformation.maxLength(800),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 12.dp,
                        end = 52.dp,
                        bottom = 12.dp,
                    ),
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    )

                )


                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                ) {
                    sendButtonContent()

                }
            }
        }
    }
}


@Composable
private fun RowScope.InputCapsule(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = {},
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
        ),
        modifier = modifier
            .weight(1f)
            .heightIn(min = 48.dp)

    ) {
        content()
    }
}