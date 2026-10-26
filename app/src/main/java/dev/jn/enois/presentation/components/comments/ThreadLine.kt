package dev.jn.enois.presentation.components.comments

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val CommentAvatarSize = 28.dp
internal val ThreadIndent = 28.dp
internal val AvatarGap = 10.dp
internal val ItemTopPadding = 6.dp
internal val ItemBottomPadding = 4.dp
internal val ActionRowHeight = 28.dp
internal val CollapseGlyphSize = 16.dp
internal val LineGap = 4.dp
internal const val MaxVisualDepth = 5


internal fun Modifier.threadLines(
    depth: Int,
    continues: List<Boolean>,
    ownLine: Boolean,
    colorOf: (level: Int) -> Color,
): Modifier = if (depth == 0 && !ownLine) this else drawBehind {
    val stroke = 1.5.dp.toPx()
    val step = ThreadIndent.toPx()
    val half = CommentAvatarSize.toPx() / 2
    val gap = LineGap.toPx()
    val height = size.height
    val visualDepth = minOf(depth, MaxVisualDepth)

    fun vertical(level: Int, from: Float, to: Float) {
        val x = level * step + half
        drawLine(colorOf(level), Offset(x, from), Offset(x, to), stroke, StrokeCap.Round)
    }

    for (level in 0 until visualDepth) {
        if (continues[level]) vertical(level, 0f, height)
    }

    if (depth in 1..MaxVisualDepth) {
        val parentLevel = depth - 1
        val x0 = parentLevel * step + half
        val avatarLeft = depth * step
        val centerY = (ItemTopPadding + CommentAvatarSize / 2).toPx()
        val radius = minOf(12.dp.toPx(), avatarLeft - gap - x0, centerY)

        if (!continues[parentLevel]) vertical(parentLevel, 0f, centerY - radius)

        val hook = Path().apply {
            moveTo(x0, centerY - radius)
            quadraticTo(x0, centerY, x0 + radius, centerY)
            lineTo(avatarLeft - gap, centerY)
        }
        drawPath(hook, colorOf(parentLevel), style = Stroke(stroke, cap = StrokeCap.Round))
    }

    if (ownLine) {
        val x = depth * step + half
        val color = colorOf(depth)
        val glyphCenterY = height - (ItemBottomPadding + ActionRowHeight / 2).toPx()
        val glyphHalf = (CollapseGlyphSize / 2).toPx()
        val top = (ItemTopPadding + CommentAvatarSize).toPx() + gap
        val aboveGlyph = glyphCenterY - glyphHalf - gap
        val belowGlyph = glyphCenterY + glyphHalf + gap

        if (aboveGlyph > top) drawLine(
            color,
            Offset(x, top),
            Offset(x, aboveGlyph),
            stroke,
            StrokeCap.Round
        )
        if (belowGlyph < height) drawLine(
            color,
            Offset(x, belowGlyph),
            Offset(x, height),
            stroke,
            StrokeCap.Round
        )
    }
}

@Composable
internal fun rememberThreadLineColors(colored: Boolean = false): (Int) -> Color {
    val scheme = MaterialTheme.colorScheme
    return remember(scheme, colored) {
        val palette = if (colored) {
            listOf(scheme.primary, scheme.tertiary, scheme.secondary, scheme.error)
                .map { lerp(it, scheme.surface, 0.35f) }
        } else listOf(scheme.outlineVariant)
        val colorOf: (Int) -> Color = { level -> palette[level % palette.size] }
        colorOf
    }
}

@Composable
internal fun CollapseGlyph(collapsed: Boolean, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(CollapseGlyphSize)) {
        val stroke = 1.5.dp.toPx()
        val radius = size.minDimension / 2 - stroke / 2
        val arm = radius * 0.5f
        drawCircle(color, radius, style = Stroke(stroke))
        drawLine(
            color,
            Offset(center.x - arm, center.y),
            Offset(center.x + arm, center.y),
            stroke,
            StrokeCap.Round
        )
        if (collapsed) {
            drawLine(
                color,
                Offset(center.x, center.y - arm),
                Offset(center.x, center.y + arm),
                stroke,
                StrokeCap.Round
            )
        }
    }
}

@Composable
internal fun BoxScope.ThreadToggleAreas(
    depth: Int,
    ancestorIds: List<String>,
    continues: List<Boolean>,
    ownId: String?,
    onToggle: (String) -> Unit,
) {
    val visualDepth = minOf(depth, MaxVisualDepth)
    Box(Modifier.matchParentSize()) {
        for (level in 0 until visualDepth) {
            when {
                continues[level] ->
                    ToggleStrip(
                        level,
                        topInset = 0.dp,
                        bottomInset = 0.dp
                    ) { onToggle(ancestorIds[level]) }

                level == depth - 1 ->
                    ToggleStrip(
                        level, topInset = 0.dp, bottomInset = 0.dp,
                        height = ItemTopPadding + CommentAvatarSize / 2,
                    ) { onToggle(ancestorIds[level]) }
            }
        }
        if (ownId != null) {
            ToggleStrip(
                level = depth,
                topInset = ItemTopPadding + CommentAvatarSize + LineGap,
                bottomInset = ItemBottomPadding + ActionRowHeight,
            ) { onToggle(ownId) }
        }
    }
}

@Composable
private fun ToggleStrip(
    level: Int,
    topInset: Dp,
    bottomInset: Dp,
    height: Dp? = null,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .offset(x = ThreadIndent * level + CommentAvatarSize / 2 - ThreadIndent / 2)
            .padding(top = topInset, bottom = bottomInset)
            .width(ThreadIndent - 4.dp)
            .then(if (height != null) Modifier.height(height) else Modifier.fillMaxHeight())
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClickLabel = "Recolher ou expandir respostas",
                role = Role.Button,
                onClick = onClick,
            )
    )
}