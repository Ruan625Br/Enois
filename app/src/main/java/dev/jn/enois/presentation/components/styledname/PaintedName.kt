package dev.jn.enois.presentation.components.styledname

import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import androidx.core.graphics.toColorInt
import dev.jn.enois.domain.model.AuthorNameStyle
import dev.jn.enois.presentation.components.avatar.fx.LocalAvatarFxClock
import dev.jn.enois.presentation.components.avatar.fx.cycle
import dev.jn.enois.presentation.components.avatar.fx.rememberFreeRunningClock

private class ResolvedPaint(val stops: Array<Pair<Float, Color>>, val vertical: Boolean)

private fun resolvePaint(spec: NameThemeSpec, userColor: Color?, primary: Color): ResolvedPaint? {
    fun Color.orPrimary() = if (this == Color.Unspecified) primary else this

    spec.paint?.let { p ->
        return ResolvedPaint(
            p.stops.map { it.first to it.second.orPrimary() }.toTypedArray(),
            p.vertical
        )
    }
    val base = spec.colors.map { it.orPrimary() }
    val colors = if (userColor != null) listOf(userColor) + base.drop(1) else base
    if (colors.size < 2) return null
    return ResolvedPaint(evenStops(colors).toTypedArray(), vertical = false)
}

@Composable
internal fun PaintedName(
    name: String,
    style: AuthorNameStyle,
    textStyle: TextStyle,
    fallbackColor: Color,
    modifier: Modifier = Modifier,
    plateInk: Color? = null,
) {
    val spec = remember(style.theme) { NameThemes.spec(style.theme) }
    val userColor = remember(style.color) { parseHexColor(style.color) }
    val primary = MaterialTheme.colorScheme.primary
    val density = LocalDensity.current

    val paint = remember(spec, userColor, primary, plateInk) {
        if (plateInk != null) null else resolvePaint(spec, userColor, primary)
    }

    val motion = spec.motion
    val clock = rememberNameClock(motion)
    val progress: () -> Float = remember(clock, motion) {
        { clock?.let { cycle(it.floatValue, motion.durationMs / 1000f) } ?: 0f }
    }

    val base = textStyle.copy(
        fontFamily = style.font?.family() ?: spec.fontFamily ?: spec.font.family(),
        fontSize = if (textStyle.fontSize.isSpecified) textStyle.fontSize * spec.fontScale
        else textStyle.fontSize,
        letterSpacing = spec.letterSpacingEm.em,
    )
    val paintColor = when {
        plateInk != null -> plateInk
        paint != null -> Color.Black
        else -> userColor ?: fallbackColor
    }

    Box(modifier.glitch(motion, progress)) {
        spec.shadows.forEach { sh ->
            val shadow = with(density) {
                Shadow(sh.color, Offset(sh.dx.dp.toPx(), sh.dy.dp.toPx()), sh.blur.dp.toPx())
            }
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = base.copy(color = sh.color.copy(alpha = 1f), shadow = shadow),
            )
        }
        Text(
            text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = base.copy(color = paintColor),
            modifier = if (paint != null) Modifier.gradientText(
                paint,
                motion,
                progress
            ) else Modifier,
        )
    }
}

private fun Modifier.gradientText(
    paint: ResolvedPaint,
    motion: NameMotion,
    progress: () -> Float,
): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@drawWithContent

        val brush = if (paint.vertical) {
            Brush.linearGradient(*paint.stops, start = Offset.Zero, end = Offset(0f, h))
        } else {
            val pan = motion as? NameMotion.Pan
            val scale = pan?.scale ?: 1f
            val x0 = if (pan != null) -(scale - 1f) * w * pan.fraction(progress()) else 0f
            Brush.linearGradient(
                *paint.stops,
                start = Offset(x0, 0f),
                end = Offset(x0 + scale * w, 0f),
                tileMode = TileMode.Repeated,
            )
        }
        drawRect(brush, blendMode = BlendMode.SrcIn)
    }

private fun Modifier.glitch(motion: NameMotion, progress: () -> Float): Modifier =
    if (motion !is NameMotion.Glitch) this else graphicsLayer {
        val p = progress()
        val (dx, dy) = when {
            p in 0.90f..0.94f -> -1f to 1f
            p in 0.94f..0.97f -> 1f to -1f
            else -> 0f to 0f
        }
        translationX = dx.dp.toPx()
        translationY = dy.dp.toPx()
    }

@Composable
private fun rememberNameClock(motion: NameMotion): FloatState? {
    if (motion is NameMotion.None || !rememberAnimationsEnabled()) return null
    return LocalAvatarFxClock.current ?: rememberFreeRunningClock()
}

@Composable
private fun rememberAnimationsEnabled(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f
    }
}

internal fun parseHexColor(hex: String?): Color? =
    hex?.let { runCatching { Color(it.toColorInt()) }.getOrNull() }