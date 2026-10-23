package dev.jn.enois.presentation.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jn.enois.presentation.chat.components.ChatInput

@Composable
fun ChatRoute(modifier: Modifier = Modifier) {
    ChatScreen(
        modifier = modifier
    )
}

@Composable
private fun ChatScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding + PaddingValues(horizontal = 8.dp))
                .fillMaxSize()
        ) {
            ChatInput(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}