package dev.jn.enois.presentation.chat.thread.components

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.jn.enois.ui.icons.AppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier) {

    var showPicker by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .imePadding()
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChatAddButton(
            onClick = { showPicker = true },
        )

        ChatTextField(
            onSendClick = onSend,
        )
    }

    if (showPicker) {
        ModalBottomSheet(
            onDismissRequest = { showPicker = false },
            dragHandle = null
        ) {
            MinimalPickerContent(
                onDismiss = { showPicker = false },
            )
        }
    }
}


@Composable
private fun ChatAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
        ),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = AppIcons.Add,
                contentDescription = null,
            )
        }
    }

}

@Composable
fun RowScope.ChatTextField(
    onSendClick: (String) -> Unit,
    modifier: Modifier = Modifier) {
    val state = rememberChatInputState()
    val activeTextState = state.textContent

    var visualLineCount by remember { mutableIntStateOf(1) }
    /*  val lineCount = remember {
          derivedStateOf {
              mapOf(activeTextState.text.toString().lines().size, visualLineCount)
          }
      }*/

    ChatInputCapsule(
        modifier = modifier
            .weight(1f)
    ) {
        Column {
            Box {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 1.dp)
                        .focusRequester(state.focusRequester),
                    state = activeTextState,
                    onTextLayout = { getResult ->
                        val result = getResult()
                        if (result != null) {
                            visualLineCount = result.lineCount
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Escreva uma mensagem",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 5),
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

                    AnimatedVisibility(
                        visible = state.textContent.text.toString().isNotEmpty(),
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
                            onClick = {
                                val text = state.textContent.text.toString()
                                if (text.isNotBlank()) {
                                    onSendClick(text)
                                    state.textContent.clearText()
                                }
                            },
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
            }
        }
    }
}

@Composable
private fun RowScope.ChatInputCapsule(
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