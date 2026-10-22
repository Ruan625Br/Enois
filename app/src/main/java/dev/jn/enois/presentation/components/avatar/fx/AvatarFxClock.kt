package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.flow.collectLatest

val LocalAvatarFxClock = compositionLocalOf<FloatState?> { null }

@Composable
fun rememberFreeRunningClock(paused: () -> Boolean = { false }): FloatState {
    val clock = remember { mutableFloatStateOf(0f) }
    val elapsed = remember { doubleArrayOf(0.0) }
    val pausedFn by rememberUpdatedState(paused)

    LaunchedEffect(Unit) {
        snapshotFlow { pausedFn() }.collectLatest { isPaused ->
            if (isPaused) return@collectLatest
            var last = withFrameNanos { it }
            while (true) {
                withFrameNanos { now ->
                    elapsed[0] += (now - last) / 1_000_000_000.0
                    last = now
                    clock.floatValue = (elapsed[0] % 3600.0).toFloat()
                }
            }
        }
    }
    return clock
}

@Composable
fun ProvideAvatarFxClock(
    paused: () -> Boolean = { false },
    content: @Composable () -> Unit,
) {
    val clock = rememberFreeRunningClock(paused)
    CompositionLocalProvider(LocalAvatarFxClock provides clock, content = content)
}