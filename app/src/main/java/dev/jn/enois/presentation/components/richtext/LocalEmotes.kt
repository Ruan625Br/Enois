package dev.jn.enois.presentation.components.richtext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalEmotes = staticCompositionLocalOf<Map<String, String>> { emptyMap() }

@Composable
fun ProvideEmotes(
    emotes: Map<String, String>,
    content: @Composable (() -> Unit)
) {
    CompositionLocalProvider(
        LocalEmotes provides emotes
    ) {
        content()
    }
}