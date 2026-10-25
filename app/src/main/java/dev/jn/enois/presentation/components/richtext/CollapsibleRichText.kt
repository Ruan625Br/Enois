package dev.jn.enois.presentation.components.richtext

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.util.hideExternalPageUrls
import kotlinx.coroutines.launch

@Composable
fun CollapsibleRichText(
    content: String,
    key: String,
    onMention: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable(key) { mutableStateOf(false) }
    var overflowing by remember(key, content) { mutableStateOf(content.length > 300) }
    val scope = rememberCoroutineScope()
    val cleaned = remember(content) { hideExternalPageUrls(content) }
    val collapsedHeight = with(LocalDensity.current) {
        MaterialTheme.typography.bodyMedium.lineHeight.toDp()
    } * 5

    Column(modifier) {
        RichText(
            content = cleaned,
            onMention = onMention,
            modifier = Modifier
                .animateContentSize(animationSpec = tween(250, easing = FastOutSlowInEasing))
                .clampHeight(collapsedHeight, collapsed = !expanded) { overflowing = it },
        )
        if (overflowing) {
            Text(
                text = if (expanded) "Ler menos" else "Ler mais",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable {
                        expanded = !expanded
                    }
                    .padding(top = 2.dp),
            )
        }
    }
}

internal fun Modifier.clampHeight(
    collapsedHeight: Dp,
    collapsed: Boolean,
    onOverflow: (Boolean) -> Unit,
): Modifier = this
    .clipToBounds()
    .layout { measurable, constraints ->
        val placeable = measurable.measure(
            constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity)
        )
        val limit = collapsedHeight.roundToPx()
        onOverflow(placeable.height > limit)
        val height = if (collapsed) minOf(placeable.height, limit) else placeable.height
        layout(placeable.width, height) { placeable.place(0, 0) }
    }