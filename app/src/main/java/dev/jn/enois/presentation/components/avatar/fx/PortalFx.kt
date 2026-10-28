package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.sin

private val rand = SeededRandom(71)

private val Mint = rgb(0x7CEBDC)
private val Lilac = rgb(0xC4A2FF)
private val MatterCore = rgb(0xE2FFF5)
private val CounterFlow = rgb(0xB49BFF)
private val Horizon = rgb(0xD6FFF4)

private val MatterEase = CubicBezierEasing(0.4f, 0f, 0.8f, 0.6f)
private val HorizonDash = PathEffect.dashPathEffect(floatArrayOf(58f, 29f, 13f, 73f), 0f)

private class Filament(val path: Path, val width: Float, val alpha: Float, val brush: Brush)


private val FILAMENTS = List(7) { i ->
    val start = i * 51 + rand.next() * 20
    val arc = 65 + rand.next() * 80
    val radius = 54 + rand.next() * 9
    val pts = List(49) { j ->
        val t = j / 48.0
        polar(radius + sin(t * PI * 3 + i) * 1.4 + sin(t * 23 + i) * 0.45, start + arc * t)
    }
    val path = Path().apply {
        moveTo(pts[0].x, pts[0].y)
        for (k in 1 until pts.size) lineTo(pts[k].x, pts[k].y)
    }
    val width = r2(0.55 + rand.next() * 0.85).toFloat()
    val alpha = r2(0.45 + rand.next() * 0.5).toFloat()
    val b = path.getBounds()
    Filament(
        path, width, alpha,
        Brush.linearGradient(
            0f to rgb(0xE2FFF4), 0.4f to rgb(0x65F7DA), 0.7f to rgb(0x62CCED), 1f to rgb(0xC9A6FF),
            start = b.topLeft, end = b.bottomRight,
        ),
    )
}

private class Matter(
    val angle: Float,
    val dur: Float,
    val delay: Float,
    val scale: Float,
    val color: Color
)

private val MATTER = List(14) { i ->
    val angle = r2(i * (360.0 / 14) + rand.next() * 12).toFloat()
    val dur = r2(1.8 + rand.next() * 1.8).toFloat()
    val delay = r2(-rand.next() * 3.6).toFloat()
    val scale = r2(0.5 + rand.next() * 0.7).toFloat()
    Matter(angle, dur, delay, scale, if (i % 3 != 0) Mint else Lilac)
}

private val FLOWS = Path().apply {
    for (i in 0 until 5) {
        val start = i * 72 + rand.next() * 20
        for (j in 0..24) {
            val t = j / 24.0
            val p = polar(74 - 20 * t, start + t * 65)
            if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
    }
}

private val COUNTER_A = Path().apply {
    moveTo(-58f, -20f); cubicTo(-57f, -51f, -25f, -68f, 4f, -64f)
    moveTo(52f, 23f); cubicTo(49f, 51f, 18f, 65f, -9f, 62f)
}
private val COUNTER_B = Path().apply {
    moveTo(-63f, 2f); quadraticTo(-66f, 26f, -49f, 42f)
    moveTo(59f, -34f); quadraticTo(68f, -17f, 65f, -6f)
}
private val MATTER_TAIL = Path().apply {
    moveTo(0f, 0f); quadraticTo(-1.5f, -5f, -4f, -8f)
}

private val Aura = Brush.radialGradient(
    0.6f to rgb(0x3B1BA5, 0f), 0.73f to rgb(0x743BDF, 0.26f),
    0.85f to rgb(0x5855D9, 0.12f), 1f to rgb(0x6366F1, 0f),
    center = Offset.Zero, radius = 79f,
)
private val Opening = Brush.radialGradient(
    0.64f to rgb(0x12103E, 0f), 0.665f to rgb(0x222056, 0.9f), 0.7f to rgb(0x268AAF, 0.8f),
    0.735f to rgb(0xB5FFEF), 0.755f to rgb(0x6FFFE2, 0.95f), 0.79f to rgb(0x33B9CC, 0.65f),
    0.86f to rgb(0x7660DC, 0.28f), 1f to rgb(0x7756CB, 0f),
    center = Offset.Zero, radius = 76f,
)
private val Depth = Brush.radialGradient(
    0.65f to rgb(0x291353, 0f), 0.88f to rgb(0x261750, 0.2f), 1f to rgb(0x111334, 0.8f),
    center = Offset.Zero, radius = 50f,
)
private val FlowBrush = Brush.radialGradient(
    0.68f to rgb(0xC1FFF0, 0.7f), 0.78f to rgb(0x71E0D1, 0.4f), 1f to rgb(0x9D83E8, 0f),
    center = Offset.Zero, radius = 75f,
)

internal fun DrawScope.drawPortal(t: Float, still: Boolean = false) {
    val k = if (still) 0f else kf(cycle(t, 3.8f), CssEaseInOut, 0f to 0f, 0.5f to 1f, 1f to 0f)
    withTransform({ scale(1f + 0.04f * k, 1f + 0.04f * k, Offset.Zero) }) {
        drawCircle(Aura, 79f, Offset.Zero, 1f - 0.4f * k)
    }
    withTransform({ scale(1f + 0.015f * k, 1f + 0.015f * k, Offset.Zero) }) {
        drawCircle(Opening, 76f, Offset.Zero, 1f - 0.12f * k)
    }
    drawCircle(Depth, 50f, Offset.Zero)


    rotate(if (still) 0f else -360f * cycle(t, 5.5f), Offset.Zero) {
        glow(1.3f, 0.8f) { w, a ->
            drawPath(
                FLOWS,
                FlowBrush,
                a,
                Stroke(1.6f + w, cap = StrokeCap.Round)
            )
        }
        drawPath(FLOWS, FlowBrush, 1f, Stroke(1.6f, cap = StrokeCap.Round))
    }

    rotate(if (still) 0f else 360f * cycle(t, 9f), Offset.Zero) {
        for (f in FILAMENTS) {
            glow(1.3f, 0.8f) { w, a ->
                drawPath(f.path, f.brush, a * f.alpha, Stroke(f.width + w, cap = StrokeCap.Round))
            }
            drawPath(f.path, f.brush, f.alpha, Stroke(f.width, cap = StrokeCap.Round))
        }
    }

    rotate(if (still) 0f else -360f * cycle(t, 14f), Offset.Zero) {
        glow(1.3f, 0.8f) { w, a ->
            drawPath(COUNTER_A, CounterFlow, a * 0.55f, Stroke(1f + w, cap = StrokeCap.Round))
            drawPath(COUNTER_B, CounterFlow, a * 0.55f, Stroke(0.55f + w, cap = StrokeCap.Round))
        }
        drawPath(COUNTER_A, CounterFlow, 0.55f, Stroke(1f, cap = StrokeCap.Round))
        drawPath(COUNTER_B, CounterFlow, 0.55f, Stroke(0.55f, cap = StrokeCap.Round))
    }

    rotate(if (still) 0f else -360f * cycle(t, 7f), Offset.Zero) {
        glow(1.3f, 0.6f) { w, a ->
            drawCircle(
                Horizon,
                53.3f,
                Offset.Zero,
                a,
                Stroke(0.75f + w * 0.6f, pathEffect = HorizonDash)
            )
        }
        drawCircle(Horizon, 53.3f, Offset.Zero, 1f, Stroke(0.75f, pathEffect = HorizonDash))
    }

    if (still) return


    for (m in MATTER) {
        val p = cycle(t, m.dur, m.delay)
        val op = kf(p, MatterEase, 0f to 0f, 0.15f to 1f, 0.65f to 1f, 1f to 0f)
        if (op <= 0.01f) continue
        val e = MatterEase.transform(p)
        val sc = 0.4f - 0.1f * e
        withTransform({
            rotate(m.angle, Offset.Zero)
            rotate(-20f + 120f * e, Offset.Zero)
            translate(77f - 26f * e, 0f)
            scale(sc, sc, Offset.Zero)
            scale(m.scale, m.scale, Offset.Zero)
        }) {
            drawPath(MATTER_TAIL, m.color, op, Stroke(0.8f, cap = StrokeCap.Round))
            drawCircle(MatterCore, 1.5f, Offset.Zero, op)
        }
    }
}