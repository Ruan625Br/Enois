package dev.jn.enois.presentation.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.jn.enois.domain.model.AccountMenuAction
import dev.jn.enois.domain.model.AccountMenuProfile
import dev.jn.enois.presentation.chat.thread.components.userAvatarSharedBounds

@Composable
fun AccountMenu(
    visible: Boolean,
    onDismiss: () -> Unit,
    profile: AccountMenuProfile?,
    modifier: Modifier = Modifier
) {
    if (profile == null) return

    val transitionState = remember {
        MutableTransitionState(false)
    }

    LaunchedEffect(visible) {
        transitionState.targetState = visible
    }

    val actions = remember {
        AccountMenuAction.all
    }

    if (transitionState.currentState || transitionState.targetState) {
        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(0, 180),
            onDismissRequest = onDismiss,
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                clippingEnabled = false
            )
        ) {
            AnimatedVisibility(
                visibleState = transitionState,
                enter = fadeIn() + scaleIn(
                    initialScale = 0.92f,
                    animationSpec = spring(
                        dampingRatio = 0.75f,
                        stiffness = 400f
                    )
                ) + slideInVertically(
                    initialOffsetY = { -it / 12 }
                ),
                exit = fadeOut() + scaleOut(
                    targetScale = 0.95f
                ) + slideOutVertically(
                    targetOffsetY = { -it / 12 }
                ),
                modifier = modifier
            ) {
                AccountMenuContent(
                    profile = profile,
                    actions = actions,
                    onAction = {})
            }
        }
    }
}
