package dev.jn.enois.presentation.feed.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import dev.jn.enois.domain.model.ReactionType
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private const val COLUMNS = 8
private val CellSize = 38.dp
private val OuterPad = 8.dp
private val InnerPad = 14.dp
private val LabelHeight = 28.dp
private const val LONG_PRESS_MS = 350L

private class PickerGeometry {
    var offsetFromAnchor: IntOffset = IntOffset.Zero
}

private class PickerPositionProvider(
    private val geometry: PickerGeometry,
    private val gapPx: Int,
    private val outerPx: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = (anchorBounds.left - outerPx)
            .coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0))
        val above = anchorBounds.top - popupContentSize.height - gapPx
        val y = if (above >= 0) above else anchorBounds.bottom + gapPx
        val position = IntOffset(x, y)
        geometry.offsetFromAnchor = position - anchorBounds.topLeft
        return position
    }
}

@Composable
fun ReactionPressTarget(
    onTap: () -> Unit,
    onReact: (ReactionType) -> Unit,
    modifier: Modifier = Modifier,
    selected: ReactionType? = null,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current
    val reactions = remember { ReactionType.entries }
    val geometry = remember { PickerGeometry() }
    val provider = remember(density) {
        with(density) { PickerPositionProvider(geometry, 8.dp.roundToPx(), OuterPad.roundToPx()) }
    }

    var pickerVisible by remember { mutableStateOf(false) }
    var hovered by remember { mutableStateOf<ReactionType?>(null) }
    val currentOnTap by rememberUpdatedState(onTap)
    val currentOnReact by rememberUpdatedState(onReact)

    Box(
        modifier = modifier.pointerInput(enabled) {
            if (!enabled) return@pointerInput

            val cell = CellSize.toPx()
            val originX = (OuterPad + InnerPad).toPx()
            val originY = (OuterPad + InnerPad + LabelHeight).toPx()
            val rows = (reactions.size + COLUMNS - 1) / COLUMNS

            fun hit(p: Offset): ReactionType? {
                val x = p.x - geometry.offsetFromAnchor.x - originX
                val y = p.y - geometry.offsetFromAnchor.y - originY
                if (x < 0 || y < 0) return null
                val col = (x / cell).toInt()
                val row = (y / cell).toInt()
                if (col >= COLUMNS || row >= rows) return null
                return reactions.getOrNull(row * COLUMNS + col)
            }

            awaitEachGesture {
                val down = awaitFirstDown()

                var released = false
                val timedOut = withTimeoutOrNull(LONG_PRESS_MS) {
                    released = waitForUpOrCancellation() != null
                } == null

                if (!timedOut) {
                    if (released) currentOnTap()
                    return@awaitEachGesture
                }

                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                pickerVisible = true
                try {
                    while (true) {
                        val change = awaitPointerEvent().changes
                            .firstOrNull { it.id == down.id } ?: break
                        change.consume()

                        val target = hit(change.position)
                        if (!change.pressed) {
                            target?.let(currentOnReact)
                            break
                        }
                        if (target != hovered) {
                            hovered = target
                            if (target != null) {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    }
                } finally {
                    pickerVisible = false
                    hovered = null
                }
            }
        }
    ) {
        content()

        if (pickerVisible) {
            Popup(
                popupPositionProvider = provider,
                properties = PopupProperties(clippingEnabled = false),
            ) {
                ReactionPickerContent(reactions, hovered, selected)
            }
        }
    }
}

@Composable
private fun ReactionPickerContent(
    reactions: List<ReactionType>,
    hovered: ReactionType?,
    selected: ReactionType?,
) {
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        appear.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 500f))
    }
    val shape = RoundedCornerShape(32.dp)
    val rows = (reactions.size + COLUMNS - 1) / COLUMNS

    Box(
        Modifier
            .padding(OuterPad)
            .graphicsLayer {
                val s = 0.85f + 0.15f * appear.value
                scaleX = s; scaleY = s
                alpha = appear.value.coerceIn(0f, 1f)
                transformOrigin = TransformOrigin(0f, 1f)
            }
            .shadow(5.dp, shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, shape)
            .padding(InnerPad)
    ) {
        Column {
            Box(
                Modifier
                    .height(LabelHeight)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                AnimatedContent(targetState = hovered, label = "reaction-label") { type ->
                    Text(
                        text = type?.label ?: "Solte para reagir",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Box(Modifier.size(CellSize * COLUMNS, CellSize * rows)) {
                reactions.forEachIndexed { index, type ->
                    ReactionCell(
                        type = type,
                        index = index,
                        hovered = hovered,
                        selected = selected == type,
                        modifier = Modifier.offset(
                            x = CellSize * (index % COLUMNS),
                            y = CellSize * (index / COLUMNS),
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ReactionCell(
    type: ReactionType,
    index: Int,
    hovered: ReactionType?,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val isHovered = hovered == type
    val anyHovered = hovered != null

    val scale by animateFloatAsState(
        targetValue = when {
            isHovered -> 1.8f
            anyHovered -> 0.8f
            else -> 1f
        },
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "scale",
    )
    val lift by animateFloatAsState(
        targetValue = if (isHovered) -10f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "lift",
    )
    val dim by animateFloatAsState(if (anyHovered && !isHovered) 0.6f else 1f, label = "dim")

    val entry = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay((index * 16L).milliseconds)
        entry.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 400f))
    }

    Box(
        modifier = modifier
            .size(CellSize)
            .zIndex(if (isHovered) 1f else 0f)
            .graphicsLayer {
                val s = scale * entry.value
                scaleX = s; scaleY = s
                translationY = lift.dp.toPx()
                alpha = entry.value.coerceIn(0f, 1f) * dim
            },
        contentAlignment = Alignment.Center,
    ) {
        if (selected && !anyHovered) {
            Box(
                Modifier
                    .size(CellSize)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    )
            )
        }
        Text(text = type.emoji, fontSize = 24.sp)
    }
}

private val ReactionType.label: String
    get() = when (this) {
        ReactionType.LIKE -> "Gostei"
        ReactionType.LOVE -> "Amei"
        ReactionType.LAUGH -> "Engraçado"
        ReactionType.ROFL -> "Hilário"
        ReactionType.WOW -> "Uau"
        ReactionType.SAD -> "Triste"
        ReactionType.ANGRY -> "Raiva"
        ReactionType.DISGUST -> "Nojo"
        ReactionType.SKEPTICAL -> "Duvidoso"
        ReactionType.CELEBRATE -> "Parabéns"
        ReactionType.SUPPORT -> "Apoio"
        ReactionType.INSIGHTFUL -> "Genial"
        ReactionType.EYES -> "Atento"
        ReactionType.NOTE -> "Anotado"
        ReactionType.FIRE -> "Fogo"
        ReactionType.CLAP -> "Palmas"
    }