package dev.jn.enois.presentation.feed.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.UserProfile
import dev.jn.enois.domain.model.asAccountMenuProfile
import dev.jn.enois.presentation.components.avatar.UserAvatar
import dev.jn.enois.presentation.profile.components.AccountMenu
import dev.jn.enois.ui.icons.AppIcons

@Composable
fun FeedToolbarActions(
    userProfile: UserProfile?,
    onConversationClick: () -> Unit,
    onAccountMenuChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonShape = RoundedCornerShape(50)
    val border = BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
    )
    val topPillSize = 48.dp
    val fullPillWidth = topPillSize * 3f
    val pillWidth by animateDpAsState(
        targetValue = fullPillWidth,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        label = "top_pill_width"
    )

    var showAccountMenu by remember { mutableStateOf(false) }


    Box {
        Surface(
            shape = buttonShape,
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = border,
            modifier = modifier
                .size(width = pillWidth, height = topPillSize)
                .clip(buttonShape)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(width = fullPillWidth, height = topPillSize)
                        .graphicsLayer {
                            this.alpha = alpha
                            val layerScale = 0.92f + alpha * 0.08f
                            scaleX = layerScale
                            scaleY = layerScale
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FeedToolbarIconButton(
                        icon = AppIcons.Notifications,
                        size = topPillSize,
                        onClick = {}
                    )

                    FeedToolbarIconButton(
                        icon = AppIcons.Inbox,
                        size = topPillSize,
                        onClick = onConversationClick
                    )

                    if (userProfile != null) {
                        Box {
                            UserProfileIcon(
                                url = userProfile.avatar ?: "",
                                size = topPillSize,
                                name = userProfile.name,
                                onClick = {
                                    showAccountMenu = !showAccountMenu
                                    onAccountMenuChange(showAccountMenu)
                                }
                            )

                            AccountMenu(
                                visible = showAccountMenu,
                                onDismiss = {
                                    showAccountMenu = false
                                    onAccountMenuChange(false)
                                },
                                profile = userProfile.asAccountMenuProfile()
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun UserProfileIcon(
    url: String,
    size: Dp,
    name: String,
    onClick: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        label = "feed_toolbar_icon_scale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        UserAvatar(
            url = url,
            userName = name,
            textStyle = MaterialTheme.typography.labelMedium,
            shape = CircleShape,
            modifier = Modifier
                .size(size * 0.75f)
        )
    }
}

@Composable
private fun FeedToolbarIconButton(
    icon: ImageVector,
    size: Dp,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        label = "feed_toolbar_icon_scale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(999.dp))
            .let { modifier ->
                if (enabled) {
                    modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            onClick()
                        }
                    )
                } else {
                    modifier
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null)
    }
}