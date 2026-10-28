package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.max
import kotlin.math.min

private val rand = SeededRandom(89)

private val Magenta = rgb(0xFF2BD6)
private val Cyan = rgb(0x22E6FF)
private val DataCyan = rgb(0x22D3EE)
private val MarkCyan = rgb(0x67E8F9)
private val ArcCyan = rgb(0xA5F3FC)
private val Ice = rgb(0xECFEFF)

private class Strip(
    val x: Float, val y: Float, val w: Float, val h: Float,
    val color: Color, val dur: Float, val delay: Float,
)

private val STRIPS = List(6) { i ->
    val y = r2(-68 + rand.next() * 136).toFloat()
    val x = r2(-75 + rand.next() * 60).toFloat()
    val w = r2(28 + rand.next() * 50).toFloat()
    val h = r2(1.2 + rand.next() * 2).toFloat()
    val dur = r2(2.1 + rand.next() * 1.6).toFloat()
    val delay = r2(-rand.next() * 3.5).toFloat()
    Strip(x, y, w, h, if (i % 2 == 1) Magenta else Cyan, dur, delay)
}


private val FacePath = Path().apply { addOval(Rect(-50f, -50f, 50f, 50f)) }

private val ScanLines = Path().apply {
    var k = -21
    while (k * 2.4f < 50f) {
        val top = max(k * 2.4f, -50f)
        val bottom = min(k * 2.4f + 0.9f, 50f)
        if (bottom > top) addRect(Rect(-50f, top, 50f, bottom))
        k++
    }
}

private val DataArcs = Path().apply {
    val r = Rect(-67f, -67f, 67f, 67f)
    arcTo(r, 270f, 45f, true)
    arcTo(r, 90f, 45f, true)
}

private val Edge = Brush.radialGradient(
    0.75f to rgb(0x0891B2, 0f),
    1f to rgb(0x0891B2, 0.45f),
    center = Offset.Zero,
    radius = 50f,
)

private val ScanBrush = Brush.verticalGradient(
    0f to rgb(0x22E6FF, 0f),
    0.5f to rgb(0xA5F3FC, 0.45f),
    1f to rgb(0x22E6FF, 0f),
    startY = -60f,
    endY = -44f,
)

private val DataDash = PathEffect.dashPathEffect(floatArrayOf(1f, 3f, 7f, 3f), 0f)
private val MarkDash = PathEffect.dashPathEffect(floatArrayOf(0.6f, 3.4f), 0f)

internal fun stepKf(p: Float, vararg s: Pair<Float, Float>): Float {
    var v = s.first().second
    for ((offset, value) in s) {
        if (p >= offset) v = value else break
    }
    return v
}

internal fun DrawScope.drawHologram(t: Float) {
    val ga = stepKf(
        cycle(t, 3.7f),
        0f to 1f, 0.31f to 0.55f, 0.32f to 1f, 0.33f to 0.75f, 0.34f to 1f,
        0.77f to 0.65f, 0.78f to 1f, 1f to 1f,
    )

    clipPath(FacePath) {
        drawCircle(Edge, 50f, Offset.Zero, ga)
        drawPath(ScanLines, MarkCyan, 0.16f * ga)
        val scanY = 104f * cycle(t, 2.6f)
        withTransform({ translate(0f, scanY) }) {
            drawRect(ScanBrush, Offset(-50f, -60f), Size(100f, 16f), ga)
        }
    }

    rotate(360f * cycle(t, 9f), Offset.Zero) {
        glow(1.3f, 0.6f) { w, a ->
            drawCircle(
                DataCyan,
                56f,
                Offset.Zero,
                a * ga,
                Stroke(0.8f + w * 0.6f, pathEffect = DataDash)
            )
        }
        drawCircle(DataCyan, 56f, Offset.Zero, ga, Stroke(0.8f, pathEffect = DataDash))
    }
    rotate(-360f * cycle(t, 14f), Offset.Zero) {
        drawCircle(MarkCyan, 62f, Offset.Zero, 0.8f * ga, Stroke(0.7f, pathEffect = MarkDash))
    }
    rotate(360f * cycle(t, 4f), Offset.Zero) {
        glow(1.3f, 1f) { w, a ->
            drawPath(DataArcs, ArcCyan, a * ga, Stroke(1.4f + w, cap = StrokeCap.Round))
        }
        drawPath(DataArcs, ArcCyan, ga, Stroke(1.4f, cap = StrokeCap.Round))
    }

    val gp = cycle(t, 2.7f)
    val gx = stepKf(gp, 0f to -0.6f, 0.89f to -2.4f, 0.91f to 1.8f, 0.93f to -1.2f, 0.95f to -0.6f)
    val gy = stepKf(gp, 0f to 0f, 0.89f to 0.8f, 0.91f to -0.6f, 0.93f to 0f)

    withTransform({ translate(gx, gy) }) {
        glow(1.3f, 0.7f) { w, a -> ring(Magenta, 50.8f, 1.6f + w, a * 0.75f * ga) }
        ring(Magenta, 50.8f, 1.6f, 0.75f * ga)
    }
    withTransform({ translate(-gx, -gy) }) {
        glow(1.3f, 0.7f) { w, a -> ring(Cyan, 50.8f, 1.6f + w, a * 0.75f * ga) }
        ring(Cyan, 50.8f, 1.6f, 0.75f * ga)
    }
    glow(1.3f, 0.6f) { w, a -> ring(Ice, 50.8f, 1.1f + w, a * ga) }
    ring(Ice, 50.8f, 1.1f, ga)

    for (s in STRIPS) {
        val p = cycle(t, s.dur, s.delay)
        val op = stepKf(p, 0f to 0.9f, 0.04f to 0.6f, 0.07f to 0f, 1f to 0f)
        if (op <= 0f) continue
        val dx = stepKf(p, 0f to 0f, 0.04f to 6f, 0.07f to 0f, 1f to 0f)
        drawRect(s.color, Offset(s.x + dx, s.y), Size(s.w, s.h), op * ga)
    }
}